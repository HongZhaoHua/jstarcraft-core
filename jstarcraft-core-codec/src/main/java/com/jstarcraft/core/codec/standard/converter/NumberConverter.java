package com.jstarcraft.core.codec.standard.converter;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.standard.StandardReader;
import com.jstarcraft.core.codec.standard.StandardWriter;
import com.jstarcraft.core.common.io.IoUtility;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 数值转换器
 * 
 * @author Birdy
 *
 */
public class NumberConverter extends StandardConverter<Number> {

    /** 0000 0000(Null标记) */
    private static final byte NULL_MARK = (byte) 0x00;

    /** 0000 0000(数值标记) */
    private static final byte NUMERICAL_MARK = (byte) 0x01;

    /**
     * 0000 1000(用于标记数值类型的正负)
     * 
     * <pre>
     * Byte,Short,Integer与Long类型使用-0代表Integer.MIN_VALUE与Long.MIN_VALUE
     * </pre>
     */
    public static final byte SWITCH_MARK = 0x08;

    /**
     * 0111 1111(长度掩码)
     * 
     * <pre>
     * 规约:
     * 如果数值小于等于LENGTH_MASK,数据会保存在LENGTH_MASK的字节中,如果数值大于LENGTH_MASK,长度会保存在LENGTH_MASK的字节中.
     * 即当LENGTH_MASK字节小于等于127时,它代表实际数据;大于127时,它代表长度数据,数据最大支持127个字节.
     * </pre>
     */
    private static final byte LENGTH_MASK = (byte) 0x7F;

    /** 1111 1111(数据掩码) */
    private static final int DATA_MASK = 0xFF;

    /** 字节位数 */
    private static final byte BYTE_BIT = 8;

    /** Short位数 */
    private static final byte SHORT_BIT = 16;

    /** Integer位数 */
    private static final byte INTEGER_BIT = 32;

    /** Long位数 */
    private static final byte LONG_BIT = 64;

    @Override
    public Number readValueFrom(StandardReader context, Type type, ClassDefinition definition) throws IOException {
        InputStream in = context.getInputStream();
        byte information = (byte) in.read();
        byte mark = getMark(information);
        if (mark == NULL_MARK) {
            return null;
        }
        boolean switchMark = ((information & SWITCH_MARK) != 0);
        if (type == Byte.class || type == byte.class) {
            byte value = readNumber(in).byteValue();
            if (switchMark && value == 0) {
                return Byte.MIN_VALUE;
            }
            return switchMark ? (byte) -value : value;
        } else if (type == Short.class || type == short.class) {
            short value = readNumber(in).shortValue();
            if (switchMark && value == 0) {
                return Short.MIN_VALUE;
            }
            return switchMark ? (short) -value : value;
        } else if (type == Integer.class || type == int.class || type == AtomicInteger.class) {
            int value = readNumber(in).intValue();
            value = switchMark ? (value == 0 ? Integer.MIN_VALUE : -value) : value;
            if (type == AtomicInteger.class) {
                return new AtomicInteger(value);
            } else {
                return value;
            }
        } else if (type == Long.class || type == long.class || type == AtomicLong.class) {
            long value = readNumber(in).longValue();
            value = switchMark ? (value == 0L ? Long.MIN_VALUE : -value) : value;
            if (type == AtomicLong.class) {
                return new AtomicLong(value);
            } else {
                return value;
            }
        } else if (type == BigInteger.class) {
            BigInteger value = (BigInteger) readNumber(in);
            return switchMark ? value.negate() : value;
        } else if (type == Float.class || type == float.class) {
            DataInput dataInput = new DataInputStream(in);
            float value = dataInput.readFloat();
            return value;
        } else if (type == Double.class || type == double.class) {
            DataInput dataInput = new DataInputStream(in);
            double value = dataInput.readDouble();
            return value;
        } else if (type == BigDecimal.class) {
            information = (byte) in.read();
            if (information >= 0) {
                byte[] data = new byte[information];
                IoUtility.read(in, data);
                String decimal = new String(data, StringUtility.CHARSET);
                BigDecimal value = new BigDecimal(decimal);
                return switchMark ? value.negate() : value;
            } else {
                throw new CodecConvertionException();
            }
        }
        String message = StringUtility.format("类型码[{}]没有对应标记码[{}]", type, mark);
        throw new CodecConvertionException(message);
    }

    @Override
    public void writeValueTo(StandardWriter context, Type type, ClassDefinition definition, Number instance) throws IOException {
        OutputStream out = context.getOutputStream();
        byte information = ClassDefinition.getMark(Specification.NUMBER);
        if (instance == null) {
            out.write(information);
            return;
        }
        if (type == Byte.class || type == byte.class) {
            byte number = instance.byteValue();
            if (number < 0) {
                information |= SWITCH_MARK | NUMERICAL_MARK;
                if (number == Byte.MIN_VALUE) {
                    number = 0;
                } else {
                    number = (byte) -number;
                }
            } else {
                information |= NUMERICAL_MARK;
            }
            out.write(information);
            writeNumber(out, number);
        } else if (type == Short.class || type == short.class) {
            short number = instance.shortValue();
            if (number < 0) {
                information |= SWITCH_MARK | NUMERICAL_MARK;
                if (number == Short.MIN_VALUE) {
                    number = 0;
                } else {
                    number = (short) -number;
                }
            } else {
                information |= NUMERICAL_MARK;
            }
            out.write(information);
            writeNumber(out, number);
        } else if (type == Integer.class || type == int.class || type == AtomicInteger.class) {
            int number = instance.intValue();
            if (number < 0) {
                information |= SWITCH_MARK | NUMERICAL_MARK;
                if (number == Integer.MIN_VALUE) {
                    number = 0;
                } else {
                    number = -number;
                }
            } else {
                information |= NUMERICAL_MARK;
            }
            out.write(information);
            writeNumber(out, number);
        } else if (type == Long.class || type == long.class || type == AtomicLong.class) {
            if (instance.longValue() == Long.MIN_VALUE) {
                information |= SWITCH_MARK | NUMERICAL_MARK;
                instance = 0;
            } else {
                long number = instance.longValue();
                if (number < 0) {
                    information |= SWITCH_MARK | NUMERICAL_MARK;
                    number = -number;
                } else {
                    information |= NUMERICAL_MARK;
                }
                instance = number;
            }
            out.write(information);
            writeNumber(out, instance);
        } else if (type == BigInteger.class) {
            BigInteger number = (BigInteger) instance;
            if (number.compareTo(BigInteger.ZERO) < 0) {
                information |= SWITCH_MARK | NUMERICAL_MARK;
                number = number.negate();
            } else {
                information |= NUMERICAL_MARK;
            }
            instance = number;
            out.write(information);
            writeNumber(out, instance);
        } else if (type == Float.class || type == float.class) {
            float number = instance.floatValue();
            information |= NUMERICAL_MARK;
            out.write(information);
            DataOutput dataOutput = new DataOutputStream(out);
            dataOutput.writeFloat(number);
        } else if (type == Double.class || type == double.class) {
            double number = instance.doubleValue();
            information |= NUMERICAL_MARK;
            out.write(information);
            DataOutput dataOutput = new DataOutputStream(out);
            dataOutput.writeDouble(number);
        } else if (type == BigDecimal.class) {
            BigDecimal number = (BigDecimal) instance;
            information |= NUMERICAL_MARK;
            out.write(information);
            String decimal = number.toString();
            byte[] data = decimal.getBytes(StringUtility.CHARSET);
            if (data.length > LENGTH_MASK) {
                throw new CodecConvertionException();
            }
            out.write(data.length);
            IoUtility.write(data, out);
        } else {
            throw new CodecConvertionException();
        }
    }

    static Number readNumber(InputStream in) throws IOException {
        byte information = (byte) in.read();
        if (information >= 0) {
            return information;
        }
        int length = information & LENGTH_MASK;
        if (in.available() < length || length < 0) {
            throw new EOFException();
        }
        if (length < (SHORT_BIT / BYTE_BIT)) {
            short value = 0;
            for (int bit = BYTE_BIT * (length - 1); bit >= 0; bit -= BYTE_BIT) {
                short data = (byte) in.read();
                value |= (data & DATA_MASK) << bit;
            }
            return value;
        } else if (length < (INTEGER_BIT / BYTE_BIT)) {
            int value = 0;
            for (int bit = BYTE_BIT * (length - 1); bit >= 0; bit -= BYTE_BIT) {
                int data = (byte) in.read();
                value |= (data & DATA_MASK) << bit;
            }
            return value;
        } else if (length < (LONG_BIT / BYTE_BIT)) {
            long value = 0;
            for (int bit = BYTE_BIT * (length - 1); bit >= 0; bit -= BYTE_BIT) {
                long data = (byte) in.read();
                value |= (data & DATA_MASK) << bit;
            }
            return value;
        } else {
            byte[] data = new byte[length];
            IoUtility.read(in, data);
            BigInteger value = new BigInteger(data);
            return value;
        }
    }

    static void writeNumber(OutputStream out, Number value) throws IOException {
        if (value instanceof BigInteger) {
            BigInteger number = (BigInteger) value;
            if (number.compareTo(BigInteger.ZERO) >= 0) {
                byte[] data = number.toByteArray();
                if (data.length > LENGTH_MASK) {
                    String message = StringUtility.format("Number数值的长度为{},不符合协议格式.", data.length);
                    throw new CodecConvertionException(message);
                }
                out.write(data.length | ~LENGTH_MASK);
                IoUtility.write(data, out);
            }
        } else {
            long number = value.longValue();
            if (number >= 0) {
                if (number <= LENGTH_MASK) {
                    byte data = (byte) number;
                    out.write(data);
                } else if (number <= Long.MAX_VALUE) {
                    for (int bit = (number <= Short.MAX_VALUE) ? SHORT_BIT : (number <= Integer.MAX_VALUE ? INTEGER_BIT : LONG_BIT); bit > 0; bit -= BYTE_BIT) {
                        if (number >>> (bit - BYTE_BIT) > 0) {
                            byte length = (byte) (bit / BYTE_BIT);
                            out.write(length | ~LENGTH_MASK);
                            for (bit -= BYTE_BIT; bit >= 0; bit -= BYTE_BIT) {
                                byte data = (byte) ((number >> bit) & DATA_MASK);
                                out.write(data);
                            }
                            return;
                        }
                    }
                    String message = StringUtility.format("Number数值的大小为{},不符合协议格式.", value);
                    throw new CodecConvertionException(message);
                }
            }
        }
    }

}
