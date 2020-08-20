package com.jstarcraft.core.common.security;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import com.jstarcraft.core.utility.KeyValue;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 安全工具(主要涉及加密/解密)
 * 
 * @author Birdy
 *
 */
public class SecurityUtility {

    private static final String AES = "AES";

    private static final String Blowfish = "Blowfish";

    private static final String DES = "DES";

    private static final String RSA = "RSA";

    private static final String MD5 = "MD5";

    private static final String SHA1 = "SHA-1";

    private static final String SHA224 = "SHA-224";

    private static final String SHA256 = "SHA-256";

    private static final String SHA384 = "SHA-384";

    private static final String SHA512 = "SHA-512";

    /**
     * 字节转换为十六进制
     * 
     * @param data
     * @return
     */
    public static String byte2Hex(byte[] data) {
        try {
            return Hex.encodeHexString(data);
        } catch (Exception exception) {
            String message = StringUtility.format("[{}]:字节转换为十六进制异常", Arrays.toString(data));
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * 十六进制转换为字节
     * 
     * @param data
     * @return
     */
    public static byte[] hex2Byte(String data) {
        try {
            return Hex.decodeHex(data.toCharArray());
        } catch (Exception exception) {
            String message = StringUtility.format("[{}]:十六进制转换为字节异常", data);
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * 使用Base64对指定数据编码
     * 
     * @param data
     * @return
     */
    public static String encodeBase64(byte[] data) {
        return Base64.encodeBase64String(data);
    }

    /**
     * 使用Base64对指定数据解码
     * 
     * @param data
     * @return
     */
    public static byte[] decodeBase64(String data) {
        return Base64.decodeBase64(data);
    }

    /**
     * 获取AES对称秘钥
     * 
     * @return
     */
    public static byte[] getAes() {
        return getAes(new SecureRandom());
    }

    /**
     * 获取AES对称秘钥
     * 
     * @param random
     * @return
     */
    public static byte[] getAes(SecureRandom random) {
        try {
            KeyGenerator aes = KeyGenerator.getInstance(AES);
            aes.init(random);
            SecretKey key = aes.generateKey();
            return key.getEncoded();
        } catch (Exception exception) {
            String message = StringUtility.format("获取AES密匙异常:[{}]");
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * AES加密
     * 
     * @param data
     * @param key
     * @return
     */
    public static byte[] encryptAes(final byte[] data, final byte[] key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception exception) {
            String message = StringUtility.format("AES加密数据异常:[{}]", Arrays.toString(data));
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * AES解密
     * 
     * @param data
     * @param key
     * @return
     */
    public static byte[] decryptAes(final byte[] data, final byte[] key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception exception) {
            String message = StringUtility.format("AES解密数据异常:[{}]", Arrays.toString(data));
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * Blowfish加密
     * 
     * @param data
     * @param key
     * @return
     */
    public static byte[] encryptBlowfish(final byte[] data, final byte[] key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, Blowfish);
            Cipher cipher = Cipher.getInstance(Blowfish);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception exception) {
            String message = StringUtility.format("Blowfish加密数据异常:[{}]", Arrays.toString(data));
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * Blowfish解密
     * 
     * @param data
     * @param key
     * @return
     */
    public static byte[] decryptBlowfish(final byte[] data, final byte[] key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, Blowfish);
            Cipher cipher = Cipher.getInstance(Blowfish);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(data);
        } catch (Exception exception) {
            String message = StringUtility.format("Blowfish解密数据异常:[{}]", Arrays.toString(data));
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * DES加密
     * 
     * @param data
     * @param key
     * @return
     */
    public static byte[] encryptDes(byte[] data, byte[] key) {
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey secretKey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance(DES);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, random);
            return cipher.doFinal(data);
        } catch (Exception exception) {
            String message = StringUtility.format("DES加密数据异常:[{}]", Arrays.toString(data));
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * DES解密
     * 
     * @param data
     * @param key
     * @return
     */
    public static byte[] decryptDes(byte[] data, byte[] key) {
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            SecretKey secretKey = keyFactory.generateSecret(desKey);
            Cipher cipher = Cipher.getInstance(DES);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, random);
            return cipher.doFinal(data);
        } catch (Exception exception) {
            String message = StringUtility.format("DES解密数据异常:[{}]", Arrays.toString(data));
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * 获取RSA非对称秘钥
     * 
     * @param size
     * @return
     */
    public static KeyValue<byte[], byte[]> getRsa(int size) {
        try {
            KeyPairGenerator rsa = KeyPairGenerator.getInstance(RSA);
            SecureRandom random = new SecureRandom();
            rsa.initialize(size, random);
            KeyPair keys = rsa.generateKeyPair();
            return new KeyValue<>(keys.getPrivate().getEncoded(), keys.getPublic().getEncoded());
        } catch (Exception exception) {
            String message = StringUtility.format("获取RAS密匙异常:[{}]");
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * RSA加密
     * 
     * @param data
     * @param key
     * @return
     */
    public static byte[] encryptRsa(final byte[] data, final byte[] key) {
        try {
            X509EncodedKeySpec secretKey = new X509EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            PublicKey publicKey = keyFactory.generatePublic(secretKey);
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception exception) {
            String message = StringUtility.format("RSA加密数据异常:[{}]", Arrays.toString(data));
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * RSA解密
     * 
     * @param data
     * @param key
     * @return
     */
    public static byte[] decryptRsa(final byte[] data, final byte[] key) {
        try {
            PKCS8EncodedKeySpec secretKey = new PKCS8EncodedKeySpec(key);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            PrivateKey privateKey = keyFactory.generatePrivate(secretKey);
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (Exception exception) {
            String message = StringUtility.format("RSA解密数据异常:[{}]", Arrays.toString(data));
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * MD5摘要
     * 
     * <pre>
     * http://www.atool.org/hash.php
     * </pre>
     * 
     * @param data
     * @return
     */
    public static byte[] signatureMd5(byte[] data) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance(MD5);
            return algorithm.digest(data);
        } catch (Exception exception) {
            String message = StringUtility.format("[{}]:MD5信息摘要异常", data);
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * SHA1摘要
     * 
     * <pre>
     * http://www.atool.org/hash.php
     * </pre>
     * 
     * @param data
     * @return
     */
    public static byte[] signatureSha1(byte[] data) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance(SHA1);
            return algorithm.digest(data);
        } catch (Exception exception) {
            String message = StringUtility.format("[{}]:SHA1信息摘要异常", data);
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * SHA224摘要
     * 
     * @param data
     * @return
     */
    public static byte[] signatureSha224(byte[] data) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance(SHA224);
            return algorithm.digest(data);
        } catch (Exception exception) {
            String message = StringUtility.format("[{}]:SHA224信息摘要异常", data);
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * SHA256摘要
     * 
     * <pre>
     * http://www.atool.org/hash.php
     * </pre>
     * 
     * @param data
     * @return
     */
    public static byte[] signatureSha256(byte[] data) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance(SHA256);
            return algorithm.digest(data);
        } catch (Exception exception) {
            String message = StringUtility.format("[{}]:SHA256信息摘要异常", data);
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * SHA384摘要
     * 
     * @param data
     * @return
     */
    public static byte[] signatureSha384(byte[] data) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance(SHA384);
            return algorithm.digest(data);
        } catch (Exception exception) {
            String message = StringUtility.format("[{}]:SHA384信息摘要异常", data);
            throw new RuntimeException(message, exception);
        }
    }

    /**
     * SHA512摘要
     * 
     * <pre>
     * http://www.atool.org/hash.php
     * </pre>
     * 
     * @param data
     * @return
     */
    public static byte[] signatureSha512(byte[] data) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance(SHA512);
            return algorithm.digest(data);
        } catch (Exception exception) {
            String message = StringUtility.format("[{}]:SHA512信息摘要异常", data);
            throw new RuntimeException(message, exception);
        }
    }

}
