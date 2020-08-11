package com.jstarcraft.core.monitor.trace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

import com.jstarcraft.core.common.conversion.csv.CsvUtility;
import com.jstarcraft.core.common.conversion.json.JsonUtility;
import com.jstarcraft.core.common.instant.CronExpression;
import com.jstarcraft.core.monitor.trace.exception.LogException;
import com.jstarcraft.core.utility.KeyValue;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Log4J2日志器
 * 
 * @author Birdy
 *
 */
@Plugin(name = "Storage", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class Log4Java2Storage extends AbstractAppender {

    public static class Builder<B extends Builder<B>> extends AbstractOutputStreamAppender.Builder<B> implements org.apache.logging.log4j.core.util.Builder<Log4Java2Storage> {

        /** CRON表达式 */
        @PluginBuilderAttribute
        @Required
        private String cron;

        /** 格式 */
        @PluginBuilderAttribute
        @Required
        private String format;

        /** 名称 */
        @PluginBuilderAttribute
        @Required
        private String names;

        /** 路径 */
        @PluginBuilderAttribute
        @Required
        private String path;

        /** 时区 */
        @PluginBuilderAttribute
        private String zone;

        /** 检测周期 */
        @PluginBuilderAttribute
        @Required
        private String period;

        @Override
        public Log4Java2Storage build() {
            return new Log4Java2Storage(getName(), getFilter(), getOrCreateLayout(), isIgnoreExceptions(), cron, format, names, path, zone, period);
        }

    }

    private static enum Format {
        CSV,

        JSON;
    }

    private static final char $ = '$';

    private static final String comma = ",";

    private static final String prefix = "{";

    private static final String suffix = "}";

    /** CRON表达式 */
    private CronExpression expression;

    /** 格式 */
    private final Format format;

    /** 名称 */
    private final String[] names;

    /** 路径 */
    private final String path;

    /** 时区 */
    private final ZoneId zone;

    /** 检测周期 */
    private final long period;

    /** 记录器缓存 */
    private final HashMap<String, KeyValue<FileOutputStream, Long>> cache = new HashMap<>();

    /** 清理线程 */
    private final Thread cleaner = new Thread(new Runnable() {

        private void clean() {
            synchronized (cache) {
                Iterator<Entry<String, KeyValue<FileOutputStream, Long>>> iterator = cache.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, KeyValue<FileOutputStream, Long>> term = iterator.next();
                    KeyValue<FileOutputStream, Long> keyValue = term.getValue();
                    long now = System.currentTimeMillis();
                    long instant = keyValue.getValue();
                    if (now - instant > period) {
                        iterator.remove();
                        FileOutputStream stream = keyValue.getKey();
                        try {
                            stream.close();
                        } catch (Exception exception) {
                            throw new LogException(exception);
                        }
                    }
                }
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(period);
                } catch (InterruptedException exception) {
                    // 中断
                    break;
                }
                clean();
            }
        }

    });

    private String getPath(Instant instant, Object... parameters) {
        instant = expression.getPreviousDateTime(instant);
        Map<String, Object> context = new HashMap<>();
        context.put(String.valueOf($), instant);
        for (int index = 0; index < parameters.length; index++) {
            context.put(names[index], parameters[index]);
        }
        StringSubstitutor substitutor = new StringSubstitutor(new StringLookup() {

            @Override
            public String lookup(String key) {
                try {
                    int index = key.indexOf($);
                    if (index > -1) {
                        String name = String.valueOf($);
                        Object instant = context.get(name);
                        String pattern = key.substring(index + 1, key.length());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern).withZone(zone);
                        return formatter.format(Instant.class.cast(instant));
                    } else {
                        String name = key;
                        Object instance = context.get(name);
                        return instance.toString();
                    }
                } catch (Exception exception) {
                    throw new LogException(exception);
                }
            }

        }, prefix, suffix, $);

        return substitutor.replace(path);
    }

    private void log(String content, Instant instant, Object object, Object... parameters) {
        String path = getPath(instant, parameters);
        synchronized (cache) {
            try {
                KeyValue<FileOutputStream, Long> keyValue = cache.get(path);
                FileOutputStream stream;
                if (keyValue == null) {
                    File file = new File(path);
                    if (!file.exists()) {
                        File directory = file.getParentFile();
                        directory.mkdirs();
                    }
                    stream = new FileOutputStream(path, true);
                    keyValue = new KeyValue<FileOutputStream, Long>(stream, instant.toEpochMilli());
                    cache.put(path, keyValue);
                } else {
                    stream = keyValue.getKey();
                }
                stream.write(content.getBytes(StringUtility.CHARSET));
                stream.write(System.lineSeparator().getBytes(StringUtility.CHARSET));
                keyValue.setValue(instant.toEpochMilli() + period);
            } catch (Exception exception) {
                throw new LogException(exception);
            }
        }
    }

    Log4Java2Storage(final String name, final Filter filter, final Layout<? extends Serializable> layout, boolean ignores, String cron, String format, String names, String path, String zone, String period) {
        super(name, filter, layout, ignores, Property.EMPTY_ARRAY);
        this.expression = new CronExpression(cron);
        this.format = Format.valueOf(format);
        if (names.indexOf($) != -1) {
            throw new LogException(names);
        }
        this.names = names.split(comma);
        this.path = path;
        this.zone = zone == null ? ZoneId.systemDefault() : ZoneId.of(zone);
        this.period = TimeUnit.MILLISECONDS.convert(Long.valueOf(period), TimeUnit.SECONDS);
        setHandler(new StorageErrorHandler());

        cleaner.setDaemon(true);
        cleaner.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                try {
                    synchronized (cache) {
                        // TODO 需要想办法等待Spring,防止日志丢失.
                        Iterator<Entry<String, KeyValue<FileOutputStream, Long>>> iterator = cache.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Entry<String, KeyValue<FileOutputStream, Long>> term = iterator.next();
                            KeyValue<FileOutputStream, Long> keyValue = term.getValue();
                            FileOutputStream stream = keyValue.getKey();
                            try {
                                stream.close();
                            } catch (Exception exception) {
                                throw new LogException(exception);
                            }
                        }
                    }
                } catch (Exception exception) {
                    throw new LogException(exception);
                }
            }

        });
    }

    @Override
    public void append(LogEvent event) {
        Object[] parameters = event.getMessage().getParameters();
        if (parameters.length != 2 + names.length) {
            throw new LogException("日志参数异常");
        }
        Instant instant = Instant.class.cast(parameters[0]);
        Object object = parameters[1];
        parameters = Arrays.copyOfRange(parameters, 2, parameters.length);

        String content;
        if (object == null) {
            content = StringUtility.EMPTY;
        } else {
            try {
                if (format == Format.CSV) {
                    content = CsvUtility.object2String(object, object.getClass());
                } else {
                    content = JsonUtility.object2String(object);
                }
            } catch (Exception exception) {
                throw new LogException("日志转换异常", exception);
            }
        }
        try {
            log(content, instant, object, parameters);
        } catch (Exception exception) {
            throw new LogException("日志设置异常", exception);
        }
    }

    @PluginBuilderFactory
    public static <B extends Builder<B>> B newBuilder() {
        return new Builder<B>().asBuilder();
    }

    // @PluginFactory TODO
    // public static NoterAppender createAppender(@PluginAttribute("name")
    // String name, @PluginElement("Filter") final Filter filter,
    // @PluginElement("Layout") Layout<? extends Serializable> layout,
    // @PluginAttribute("ignoreExceptions") boolean ignores,
    // @PluginAttribute("cron") String cron, @PluginAttribute("format") String
    // format, @PluginAttribute("names") String names, @PluginAttribute("path")
    // String path, @PluginAttribute("zone") String zone) {
    // if (name == null) {
    // throw new IllegalArgumentException();
    // }
    // if (layout == null) {
    // layout = PatternLayout.createDefaultLayout();
    // }
    //
    // return new NoterAppender(name, filter, layout, ignores, cron, format,
    // names, path, zone);
    // }

}
