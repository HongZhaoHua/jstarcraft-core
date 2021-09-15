package com.jstarcraft.core.common.bit;

import java.util.Arrays;
import java.util.List;

import org.redisson.Redisson;
import org.redisson.api.RFuture;
import org.redisson.api.RScript;
import org.redisson.api.RScript.Mode;
import org.redisson.api.RScript.ReturnType;
import org.redisson.client.codec.ByteArrayCodec;
import org.redisson.client.codec.IntegerCodec;
import org.redisson.client.protocol.RedisCommands;
import org.redisson.command.CommandAsyncExecutor;

/**
 * 
 * @author Birdy
 *
 */
public class GlobalByteArrayMap implements BitMap<byte[]> {

    private static String getBitLua;

    private static String setBitLua;

    private static String unsetBitLua;

    static {
        StringBuilder buffer = new StringBuilder();

        buffer.setLength(0);
        buffer.append("local index;");
        buffer.append("local size = #ARGV;");
        buffer.append("local bits = {};");
        buffer.append("for index = 1, size, 1 do");
        buffer.append("    bits[index] = redis.call('getbit', KEYS[1], ARGV[index]);");
        buffer.append("end;");
        buffer.append("return bits;");
        getBitLua = buffer.toString();

        buffer.setLength(0);
        buffer.append("local index;");
        buffer.append("local size = #ARGV;");
        buffer.append("for index = 1, size, 1 do");
        buffer.append("    redis.call('setbit', KEYS[1], ARGV[index], 1);");
        buffer.append("end;");
        buffer.append("return 'null';");
        setBitLua = buffer.toString();

        buffer.setLength(0);
        buffer.append("local index;");
        buffer.append("local size = #ARGV;");
        buffer.append("for index = 1, size, 1 do");
        buffer.append("    redis.call('setbit', KEYS[1], ARGV[index], 0);");
        buffer.append("end;");
        buffer.append("return 'null';");
        unsetBitLua = buffer.toString();
    }

    private RScript bits;

    private CommandAsyncExecutor executor;

    private String name;

    private String getBit;

    private String setBit;

    private String unsetBit;

    private List<Object> keys;

    private int capacity;

    public GlobalByteArrayMap(Redisson redis, String name, byte[] bytes, int capacity) {
        this.bits = redis.getScript();
        this.executor = redis.getCommandExecutor();
        this.name = name;
        this.getBit = bits.scriptLoad(getBitLua);
        this.setBit = bits.scriptLoad(setBitLua);
        this.unsetBit = bits.scriptLoad(unsetBitLua);
        this.keys = Arrays.asList(name);
        this.capacity = capacity;
        RFuture<Void> future = executor.readAsync(name, ByteArrayCodec.INSTANCE, RedisCommands.SET, name, bytes);
        executor.get(future);
    }

    public GlobalByteArrayMap(Redisson redis, String name, int capacity) {
        assert capacity > 0;
        this.bits = redis.getScript();
        this.executor = redis.getCommandExecutor();
        this.name = name;
        this.getBit = bits.scriptLoad(getBitLua);
        this.setBit = bits.scriptLoad(setBitLua);
        this.unsetBit = bits.scriptLoad(unsetBitLua);
        this.keys = Arrays.asList(name);
        this.capacity = capacity;
    }

    @Override
    public boolean get(int index) {
        Integer[] parameters = new Integer[] { index };
        List<Long> hits = bits.evalSha(Mode.READ_WRITE, getBit, ReturnType.MULTI, keys, parameters);
        return hits.get(0) == 1L;
    }

    @Override
    public void get(int[] indexes, boolean[] values) {
        Integer[] parameters = new Integer[indexes.length];
        for (int index = 0, size = indexes.length; index < size; index++) {
            parameters[index] = indexes[index];
        }
        List<Long> hits = bits.evalSha(Mode.READ_WRITE, getBit, ReturnType.MULTI, keys, parameters);
        for (int index = 0, size = indexes.length; index < size; index++) {
            values[index] = hits.get(index) == 1L;
        }
    }

    @Override
    public void set(int index) {
        Integer[] parameters = new Integer[] { index };
        bits.evalSha(Mode.READ_WRITE, setBit, ReturnType.MULTI, keys, parameters);
    }

    @Override
    public void set(int[] indexes) {
        Integer[] parameters = new Integer[indexes.length];
        for (int index = 0, size = indexes.length; index < size; index++) {
            parameters[index] = indexes[index];
        }
        bits.evalSha(Mode.READ_WRITE, setBit, ReturnType.MULTI, keys, parameters);
    }

    @Override
    public void unset(int index) {
        Integer[] parameters = new Integer[] { index };
        bits.evalSha(Mode.READ_WRITE, unsetBit, ReturnType.MULTI, keys, parameters);
    }

    @Override
    public void unset(int[] indexes) {
        Integer[] parameters = new Integer[indexes.length];
        for (int index = 0, size = indexes.length; index < size; index++) {
            parameters[index] = indexes[index];
        }
        bits.evalSha(Mode.READ_WRITE, unsetBit, ReturnType.MULTI, keys, parameters);
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public int count() {
        RFuture<Long> future = executor.readAsync(name, IntegerCodec.INSTANCE, RedisCommands.BITCOUNT, name);
        return executor.get(future).intValue();
    }

    @Override
    public byte[] bits() {
        RFuture<byte[]> future = executor.readAsync(name, ByteArrayCodec.INSTANCE, RedisCommands.GET, name);
        byte[] from = executor.get(future);
        int size = capacity % Byte.SIZE == 0 ? capacity / Byte.SIZE : capacity / Byte.SIZE + 1;
        byte[] to = new byte[size];
        if (from != null) {
            System.arraycopy(from, 0, to, 0, from.length);
        }
        return to;
    }

}