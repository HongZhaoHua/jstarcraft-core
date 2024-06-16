package com.jstarcraft.core.common.security.captcha;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;

public abstract class AbstractCaptcha implements Captcha {

    /**
     * 图片的宽度
     */
    protected int width;
    /**
     * 图片的高度
     */
    protected int height;
    /**
     * 验证码干扰元素个数
     */
    protected int interfere;

    /**
     * 背景色
     */
    protected Color color;
    /**
     * 字体
     */
    protected Font font;
    /**
     * 文字透明度
     */
    protected AlphaComposite transparency;

    /**
     * 构造
     *
     * @param width          图片宽
     * @param height         图片高
     * @param generator      验证码生成器
     * @param interfereCount 验证码干扰元素个数
     */
    public AbstractCaptcha(int width, int height, int interfere) {
        this.width = width;
        this.height = height;
        this.interfere = interfere;
        // 字体高度设为验证码高度-2，留边距
        this.font = new Font(Font.SANS_SERIF, Font.PLAIN, (int) (this.height * 0.75));
    }

    /**
     * 设置背景色
     *
     * @param background 背景色
     * @since 4.1.22
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * 自定义字体
     *
     * @param font 字体
     */
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * 设置文字透明度
     *
     * @param transparency 文字透明度，取值0~1，1表示不透明
     * @since 4.5.17
     */
    public void setTransparency(float transparency) {
        this.transparency = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency);
    }

}
