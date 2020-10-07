package com.jstarcraft.core.codec.kryo;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.exception.CodecException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * Kryo格式编解码器
 * 
 * @author Birdy
 */
public class KryoContentCodec implements ContentCodec {

    private static final Logger LOGGER = LoggerFactory.getLogger(KryoContentCodec.class);

    private final CodecDefinition codecDefinition;

    private final Kryo kryo;

    public KryoContentCodec(CodecDefinition definition) {
        this(true, 5, definition);
    }

    public KryoContentCodec(boolean registration, int dimension, CodecDefinition definition) {
        this.codecDefinition = definition;
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(registration);
        if (registration) {
            for (ClassDefinition classDefinition : definition.getClassDefinitions()) {
                Class<?> clazz = classDefinition.getType();
                if (clazz == void.class || clazz == Void.class) {
                    // TODO
                    continue;
                }
                kryo.register(clazz);
                if (clazz.isPrimitive()) {
                    for (int index = 0; index < dimension; index++) {
                        Object array = Array.newInstance(clazz, 0);
                        kryo.register(array.getClass());
                        clazz = array.getClass();
                    }
                } else {
                    Type type = clazz;
                    for (int index = 0; index < dimension; index++) {
                        type = TypeUtility.genericArrayType(type);
                        kryo.register(TypeUtility.getRawType(type, null));
                    }
                }
            }
        }
        this.kryo = kryo;
    }

    @Override
    public Object decode(Type type, byte[] content) {
        if (content.length == 0) {
            return null;
        }
        try (Input byteBufferInput = new Input(content)) {
            Specification specification = Specification.getSpecification(type);
            if (specification == Specification.TYPE) {
                Type value = TypeUtility.string2Type(kryo.readObject(byteBufferInput, String.class));
                return value;
            } else {
                if (kryo.isRegistrationRequired()) {
                    return kryo.readObject(byteBufferInput, TypeUtility.getRawType(type, null));
                } else {
                    return kryo.readClassAndObject(byteBufferInput);
                }
            }
        } catch (Exception exception) {
            String message = "Kryo解码异常";
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public Object decode(Type type, InputStream stream) {
        try (Input byteBufferInput = new Input(stream)) {
            if (stream.available() == 0) {
                return null;
            }
            Specification specification = Specification.getSpecification(type);
            if (specification == Specification.TYPE) {
                Type value = TypeUtility.string2Type(kryo.readObject(byteBufferInput, String.class));
                return value;
            } else {
                if (kryo.isRegistrationRequired()) {
                    return kryo.readObject(byteBufferInput, TypeUtility.getRawType(type, null));
                } else {
                    return kryo.readClassAndObject(byteBufferInput);
                }
            }
        } catch (Exception exception) {
            String message = "Kryo解码异常";
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public byte[] encode(Type type, Object content) {
        if (content == null) {
            return new byte[] {};
        }
        try (Output byteBufferOutput = new Output(1024, -1)) {
            Specification specification = Specification.getSpecification(type);
            if (specification == Specification.TYPE) {
                kryo.writeObject(byteBufferOutput, TypeUtility.type2String((Type) content));
                byte[] value = byteBufferOutput.toBytes();
                return value;
            } else {
                if (kryo.isRegistrationRequired()) {
                    kryo.writeObject(byteBufferOutput, content);
                    return byteBufferOutput.toBytes();
                } else {
                    kryo.writeClassAndObject(byteBufferOutput, content);
                    return byteBufferOutput.toBytes();
                }
            }
        } catch (Exception exception) {
            String message = "Kryo编码异常";
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

    @Override
    public void encode(Type type, Object content, OutputStream stream) {
        try (Output byteBufferOutput = new Output(stream)) {
            if (content == null) {
                return;
            }
            Specification specification = Specification.getSpecification(type);
            if (specification == Specification.TYPE) {
                kryo.writeObject(byteBufferOutput, TypeUtility.type2String((Type) content));
            } else {
                if (kryo.isRegistrationRequired()) {
                    kryo.writeObject(byteBufferOutput, content);
                } else {
                    kryo.writeClassAndObject(byteBufferOutput, content);
                }
            }
        } catch (Exception exception) {
            String message = "Kryo编码异常";
            LOGGER.error(message, exception);
            throw new CodecException(message, exception);
        }
    }

}
