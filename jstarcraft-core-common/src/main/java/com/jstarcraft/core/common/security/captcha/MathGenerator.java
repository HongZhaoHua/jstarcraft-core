package com.jstarcraft.core.common.security.captcha;

import com.jstarcraft.core.utility.KeyValue;
import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

public class MathGenerator implements CodeGenerator {

    private static final String operators = "+-";

    /** 参与计算数字最大长度 */
    private final int numberLength;

    /**
     * 构造
     */
    public MathGenerator() {
        this(2);
    }

    /**
     * 构造
     * 
     * @param numberLength 参与计算最大数字位数
     */
    public MathGenerator(int numberLength) {
        this.numberLength = numberLength;
    }

    @Override
    public KeyValue<String, String> generate() {
        final int limit = getLimit();
        int left = RandomUtility.randomInteger(limit);
        int right = RandomUtility.randomInteger(limit);
        String operator = RandomUtility.randomString(operators, 1);
        int value = 0;
        switch (operator) {
        case "+": {
            value = left + right;
            break;
        }
        case "-": {
            value = left - right;
            break;
        }
        default: {
            throw new UnsupportedOperationException();
        }
        }
        StringBuilder buffer = new StringBuilder()

                .append(StringUtility.rightPad(String.valueOf(left), this.numberLength, StringUtility.SPACE))

                .append(operator)

                .append(StringUtility.leftPad(String.valueOf(right), this.numberLength, StringUtility.SPACE))

                .append('=');
        String key = buffer.toString();
        return new KeyValue<>(key, String.valueOf(value));
    }

    /**
     * 获取验证码长度
     *
     * @return 验证码长度
     */
    public int getLength() {
        return this.numberLength * 2 + 2;
    }

    /**
     * 根据长度获取参与计算数字最大值
     * 
     * @return 最大值
     */
    private int getLimit() {
        return Integer.parseInt("1" + StringUtility.repeat('0', this.numberLength));
    }

}
