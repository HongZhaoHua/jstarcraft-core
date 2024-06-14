package com.jstarcraft.core.common.security.captcha;

public abstract class AbstractGenerator implements CodeGenerator {

    protected final static String random = "0123456789abcdefghijklmnopqrstuvwxyz";

    /** 基础字符集合，用于随机获取字符串的字符集合 */
    protected final String base;
    /** 验证码长度 */
    protected final int length;

    /**
     * 构造，使用字母+数字做为基础
     *
     * @param count 生成验证码长度
     */
    public AbstractGenerator(int count) {
        this(random, count);
    }

    /**
     * 构造
     *
     * @param base 基础字符集合，用于随机获取字符串的字符集合
     * @param length  生成验证码长度
     */
    public AbstractGenerator(String base, int length) {
        this.base = base;
        this.length = length;
    }

    /**
     * 获取长度验证码
     *
     * @return 验证码长度
     */
    public int getLength() {
        return this.length;
    }

}
