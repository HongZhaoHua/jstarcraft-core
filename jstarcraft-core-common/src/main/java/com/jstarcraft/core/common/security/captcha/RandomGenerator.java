package com.jstarcraft.core.common.security.captcha;

import com.jstarcraft.core.utility.KeyValue;
import com.jstarcraft.core.utility.RandomUtility;

public class RandomGenerator extends AbstractGenerator {

    /**
     * 构造，使用字母+数字做为基础
     *
     * @param count 生成验证码长度
     */
    public RandomGenerator(int count) {
        super(count);
    }

    /**
     * 构造
     *
     * @param base   基础字符集合，用于随机获取字符串的字符集合
     * @param length 生成验证码长度
     */
    public RandomGenerator(String base, int length) {
        super(base, length);
    }

    @Override
    public KeyValue<String, String> generate() {
        String code = RandomUtility.randomString(this.base, this.length);
        return new KeyValue<>(code, code);
    }

}
