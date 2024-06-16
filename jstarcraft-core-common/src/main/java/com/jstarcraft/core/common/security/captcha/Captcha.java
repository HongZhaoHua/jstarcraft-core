package com.jstarcraft.core.common.security.captcha;

import java.awt.image.BufferedImage;

public interface Captcha {

    // 英文Bitmap（位图）的简写，它是Windows操作系统中的标准图像文件格式
    public static final String IMAGE_FORMAT_BMP = "bmp";
    // 图形交换格式
    public static final String IMAGE_FORMAT_GIF = "gif";
    // 联合照片专家组
    public static final String IMAGE_FORMAT_JPG = "jpg";
    // 可移植网络图形
    public static final String IMAGE_FORMAT_PNG = "png";
    // Photoshop的专用格式Photoshop
    public static final String IMAGE_FORMAT_PSD = "psd";

    /**
     * 创建验证码，实现类需同时生成随机验证码字符串和验证码图片
     */
    BufferedImage generateImage(String content);

}
