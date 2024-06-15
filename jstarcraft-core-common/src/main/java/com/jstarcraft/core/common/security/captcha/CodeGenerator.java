package com.jstarcraft.core.common.security.captcha;

import com.jstarcraft.core.utility.KeyValue;

public interface CodeGenerator {

    /**
     * 生成验证码
     *
     * @return 验证码
     */
    KeyValue<String, String> generate();

}
