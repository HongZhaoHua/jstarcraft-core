package com.jstarcraft.core.common.security;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.utility.KeyValue;
import com.jstarcraft.core.utility.StringUtility;

public class SecurityUtilityTestCase {

    private String content = "加密解密测试-EncryptDecryptTest";

    @Test
    public void testAes() {
        byte[] aseKey = SecurityUtility.getAes();
        byte[] data = content.getBytes(StringUtility.CHARSET);
        data = SecurityUtility.encryptAes(data, aseKey);
        data = SecurityUtility.decryptAes(data, aseKey);
        Assert.assertThat(new String(data, StringUtility.CHARSET), CoreMatchers.equalTo(content));
    }

    @Test
    public void testRsa() {
        KeyValue<byte[], byte[]> rasKeys = SecurityUtility.getRsa(512);
        byte[] data = content.getBytes(StringUtility.CHARSET);
        data = SecurityUtility.encryptRsa(data, rasKeys.getValue());
        data = SecurityUtility.decryptRsa(data, rasKeys.getKey());
        Assert.assertThat(new String(data, StringUtility.CHARSET), CoreMatchers.equalTo(content));
    }

    @Test
    public void testMd5() {
        byte[] data = content.getBytes(StringUtility.CHARSET);
        String signature = SecurityUtility.byte2Hex(SecurityUtility.signatureMd5(data));
        Assert.assertThat(signature, CoreMatchers.equalTo("5a1ddc9c39e678f24421d78c52fbf151"));
    }

    @Test
    public void testSha1() {
        byte[] data = content.getBytes(StringUtility.CHARSET);
        String signature = SecurityUtility.byte2Hex(SecurityUtility.signatureSha1(data));
        Assert.assertThat(signature, CoreMatchers.equalTo("2f04674652d9213c3f7c077a23930db0ec761538"));
    }

    @Test
    public void testSha2() {
        {
            byte[] data = content.getBytes(StringUtility.CHARSET);
            String signature = SecurityUtility.byte2Hex(SecurityUtility.signatureSha256(data));
            Assert.assertThat(signature, CoreMatchers.equalTo("d41c71dbf55754d25af265f53c001e8061b3283c66092852d3254f58fab4c21f"));
        }
        {
            byte[] data = content.getBytes(StringUtility.CHARSET);
            String signature = SecurityUtility.byte2Hex(SecurityUtility.signatureSha512(data));
            Assert.assertThat(signature, CoreMatchers.equalTo("4a6750f5b12919bf5fe173cd323c25c1b43dca0bda4ae69cc1aa7b8e01c60314b2096c15509fcc14458303c9d04262088ee0d4a072ab7771b6b169817eb4e66a"));
        }
    }

}
