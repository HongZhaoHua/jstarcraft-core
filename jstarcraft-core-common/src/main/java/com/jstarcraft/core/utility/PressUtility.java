package com.jstarcraft.core.utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 压缩工具
 * 
 * @author Birdy
 */
public final class PressUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(PressUtility.class);

    /** 缓冲区大小 */
    private static final int BUFFER_SIZE = 1024;

    /** 任务线程池 */
    private static final ExecutorService executorService = Executors.newCachedThreadPool(new NameThreadFactory("PressUtility"));

    /**
     * 按照指定的时间解压指定的数据
     * 
     * @param datas
     * @param time
     * @param unit
     * @return
     */
    public static byte[] unzip(final byte[] datas, long time, TimeUnit unit) {
        Future<byte[]> future = executorService.submit(new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                Inflater inflater = new Inflater();
                inflater.setInput(datas);
                byte[] bytes = new byte[BUFFER_SIZE];
                try (ByteArrayOutputStream stream = new ByteArrayOutputStream(BUFFER_SIZE)) {
                    while (!inflater.finished()) {
                        int count = inflater.inflate(bytes);
                        stream.write(bytes, 0, count);
                    }
                    inflater.end();
                    return stream.toByteArray();
                }
            }
        });
        try {
            return future.get(time, unit);
        } catch (InterruptedException exception) {
            throw new IllegalStateException("解压中断:", exception);
        } catch (ExecutionException exception) {
            throw new IllegalStateException("解压异常:", exception);
        } catch (TimeoutException exception) {
            throw new IllegalStateException("解压超时", exception);
        }
    }

    /**
     * 按照指定的级别压缩指定的数据
     * 
     * @param datas
     * @param level
     * @return
     */
    public static byte[] zip(byte[] datas, int level) {
        if (level < Deflater.NO_COMPRESSION || level > Deflater.BEST_COMPRESSION) {
            String message = StringUtility.format("非法的压缩等级[{}]", level);
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        }
        Deflater deflater = new Deflater(level);
        deflater.setInput(datas);
        deflater.finish();
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream(BUFFER_SIZE)) {
            byte[] bytes = new byte[BUFFER_SIZE];
            while (!deflater.finished()) {
                int count = deflater.deflate(bytes);
                stream.write(bytes, 0, count);
            }
            deflater.end();
            return stream.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("压缩异常", exception);
        }
    }

}
