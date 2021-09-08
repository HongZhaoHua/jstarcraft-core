package com.jstarcraft.core.common.bloomfilter.global;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RScript;
import org.redisson.api.RScript.Mode;
import org.redisson.api.RScript.ReturnType;
import org.redisson.client.codec.ByteArrayCodec;

import com.jstarcraft.core.common.bloomfilter.BloomFilter;
import com.jstarcraft.core.common.hash.StringHashFunction;

/**
 * 基于Redis的布隆过滤器
 * 
 * @author Birdy
 *
 */
public class ScriptGlobalBloomFilter implements BloomFilter {

    private static String getBitLua;

    private static String setBitLua;

    static {
        StringBuilder buffer = new StringBuilder();

        buffer.setLength(0);
        buffer.append("local count = 0;");
        buffer.append("local size = #ARGV;");
        buffer.append("local step = ARGV[1];");
        buffer.append("for index = 1, size, step do");
        buffer.append("    local hit = true;");
        buffer.append("    for offset = 1, step, 1 do");
        buffer.append("        local bit = redis.call('getbit', KEYS[1], ARGV[index + offset]);");
        buffer.append("        if (bit == 0) then");
        buffer.append("            hit = false;");
        buffer.append("            break;");
        buffer.append("        end;");
        buffer.append("    end;");
        buffer.append("    if (hit) then");
        buffer.append("        count = count + 1;");
        buffer.append("    end;");
        buffer.append("end;");
        buffer.append("return count..'';");
        getBitLua = buffer.toString();

        buffer.setLength(0);
        buffer.append("local count = 0;");
        buffer.append("local size = #ARGV;");
        buffer.append("local step = ARGV[1];");
        buffer.append("for index = 1, size, step do");
        buffer.append("    for offset = 1, step, 1 do");
        buffer.append("        redis.call('setbit', KEYS[1], ARGV[index + offset], 1);");
        buffer.append("    end;");
        buffer.append("end;");
        buffer.append("return 'null';");
        setBitLua = buffer.toString();
    }

    private RScript script;

    private String getBit;

    private String setBit;

    private List<Object> keys;

    private RBucket<byte[]> bytes;

    private int capacity;

    private StringHashFunction[] functions;

    public ScriptGlobalBloomFilter(Redisson redisson, String name, int capacity, StringHashFunction... functions) {
        this.script = redisson.getScript();
        this.getBit = script.scriptLoad(getBitLua);
        this.setBit = script.scriptLoad(setBitLua);
        this.keys = Arrays.asList(name);
        this.bytes = redisson.getBucket(name, ByteArrayCodec.INSTANCE);
        this.capacity = capacity;
        this.functions = functions;
    }

    private Integer[] values(String data) {
        Integer[] values = new Integer[functions.length + 1];
        values[0] = functions.length;
        int index = 1;
        for (StringHashFunction function : functions) {
            int hash = function.hash(data);
            values[index++] = Math.abs(hash % capacity);
        }
        return values;
    }

    private Integer[] values(Collection<String> datas) {
        Integer[] values = new Integer[datas.size() * functions.length + 1];
        values[0] = datas.size() * functions.length;
        int index = 1;
        for (String data : datas) {
            for (StringHashFunction function : functions) {
                int hash = function.hash(data);
                values[index++] = Math.abs(hash % capacity);
            }
        }
        return values;
    }

    @Override
    public boolean getBit(String data) {
        Integer[] values = values(data);
        Integer count = script.evalSha(Mode.READ_WRITE, getBit, ReturnType.VALUE, keys, values);
        return count == 1;
    }

    @Override
    public int getBits(Collection<String> datas) {
        Integer[] values = values(datas);
        Integer count = script.evalSha(Mode.READ_WRITE, getBit, ReturnType.VALUE, keys, values);
        return count;
    }

    @Override
    public void putBit(String data) {
        Integer[] values = values(data);
        script.evalSha(Mode.READ_WRITE, setBit, ReturnType.VALUE, keys, values);
    }

    @Override
    public void putBits(Collection<String> datas) {
        Integer[] values = values(datas);
        script.evalSha(Mode.READ_WRITE, setBit, ReturnType.VALUE, keys, values);
    }

    @Override
    public int bitSize() {
        return capacity;
    }

    @Override
    public int hashSize() {
        return functions.length;
    }

    public byte[] getBytes() {
        return bytes.get();
    }

}
