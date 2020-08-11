package com.jstarcraft.core.monitor.trace;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.apache.log4j.FileAppender;
import org.apache.log4j.spi.LoggingEvent;

import com.jstarcraft.core.common.conversion.csv.CsvUtility;
import com.jstarcraft.core.common.conversion.json.JsonUtility;
import com.jstarcraft.core.common.instant.CronExpression;
import com.jstarcraft.core.monitor.trace.exception.LogException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Log4J1日志器
 * 
 * @author Birdy
 *
 */
public class Log4Java1Storage extends FileAppender {

    private static enum Format {
        CSV,

        JSON;
    }

    private static final char $ = '$';

    private static final String comma = ",";

    private static final String prefix = "{";

    private static final String suffix = "}";

    private static final Field MESSAGE_FIELD;

    static {
        try {
            MESSAGE_FIELD = LoggingEvent.class.getDeclaredField("renderedMessage");
            MESSAGE_FIELD.setAccessible(true);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    /** CRON表达式 */
    private CronExpression expression;

    /** 格式 */
    private Format format;

    /** 名称 */
    private String[] names = new String[] {};

    /** 路径 */
    private String path;

    /** 时区 */
    private ZoneId zone = ZoneId.systemDefault();

    /** 记录器缓存 */
    private WeakHashMap<String, FileAppender> cache = new WeakHashMap<>();

    public void setCron(String cron) {
        this.expression = new CronExpression(cron);
    }

    public void setFormat(String format) {
        this.format = Format.valueOf(format);
    }

    public void setNames(String names) {
        if (names.indexOf($) != -1) {
            throw new LogException(names);
        }
        this.names = names.split(comma);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setZone(String zone) {
        this.zone = zone == null ? ZoneId.systemDefault() : ZoneId.of(zone);
    }

    /**
     * 路径构建
     * 
     * @param name
     * @param object
     * @return
     */
    private String getPath(Instant instant, Object object, Object... parameters) {
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

    private FileAppender log(Instant instant, Object object, Object... parameters) {
        String path = getPath(instant, object, parameters);
        FileAppender logger = cache.get(path);
        if (logger == null) {
            try {
                logger = new FileAppender(layout, path);
            } catch (IOException exception) {
                throw new LogException("日志记录异常", exception);
            }
            logger.activateOptions();
            cache.put(path, logger);
        } else {
            // TODO 此处应该不可能执行
            String file = logger.getFile();
            if (file == null || !file.equals(path)) {
                logger.setFile(path);
                logger.activateOptions();
            }
        }
        return logger;
    }

    @Override
    public void activateOptions() {
        // 阻止任何行为
    }

    @Override
    public synchronized void doAppend(LoggingEvent event) {
        Object[] parameters = (Object[]) event.getMessage();
        if (parameters.length != 2 + names.length) {
            throw new LogException("日志参数异常");
        }

        Instant instant = Instant.class.cast(parameters[0]);
        Object object = parameters[1];
        parameters = Arrays.copyOfRange(parameters, 2, parameters.length);

        if (StringUtility.isBlank(name)) {
            // 未指定日志名称,使用类名
            name = object.getClass().getSimpleName();
        }
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

        FileAppender appender = log(instant, object, parameters);
        if (StringUtility.isBlank(content)) {
            // 忽略空行
            return;
        }
        try {
            MESSAGE_FIELD.set(event, content);
        } catch (Exception exception) {
            throw new LogException("日志设置异常", exception);
        }
        appender.doAppend(event);
    }

}
