package com.jstarcraft.core.common.security.captcha;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import com.jstarcraft.core.common.io.IoUtility;
import com.jstarcraft.core.utility.KeyValue;

public abstract class AbstractCaptcha implements Captcha {

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
     * 字体
     */
    protected Font font;
    /**
     * 验证码
     */
    protected String code;
    /**
     * 验证码图片
     */
    protected byte[] bytes;
    /**
     * 验证码生成器
     */
    protected CodeGenerator generator;
    /**
     * 背景色
     */
    protected Color color;
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
    public AbstractCaptcha(int width, int height, CodeGenerator generator, int interfere) {
        this.width = width;
        this.height = height;
        this.generator = generator;
        this.interfere = interfere;
        // 字体高度设为验证码高度-2，留边距
        this.font = new Font(Font.SANS_SERIF, Font.PLAIN, (int) (this.height * 0.75));
    }

    @Override
    public BufferedImage createCode() {
        generateCode();
        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream(); ImageOutputStream stream = ImageIO.createImageOutputStream(bytes)) {
            BufferedImage image = createImage(this.code);
            ImageIO.write(image, IMAGE_FORMAT_JPG, stream);
            this.bytes = bytes.toByteArray();
            return image;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * 生成验证码字符串
     *
     * @since 3.3.0
     */
    protected void generateCode() {
        KeyValue<String, String> keyValue = generator.generate();
        this.code = keyValue.getKey();
    }

    /**
     * 根据生成的code创建验证码图片
     *
     * @param code 验证码
     * @return Image
     */
    protected abstract BufferedImage createImage(String code);

    @Override
    public String getCode() {
        if (null == this.code) {
            createCode();
        }
        return this.code;
    }

    @Override
    public void write(OutputStream out) {
        try {
            IoUtility.write(bytes, out);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * 获取图形验证码图片bytes
     *
     * @return 图形验证码图片bytes
     * @since 4.5.17
     */
    public byte[] getImageBytes() {
        if (null == this.bytes) {
            createCode();
        }
        return this.bytes;
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
     * 获取验证码生成器
     *
     * @return 验证码生成器
     */
    public CodeGenerator getGenerator() {
        return generator;
    }

    /**
     * 设置验证码生成器
     *
     * @param generator 验证码生成器
     */
    public void setGenerator(CodeGenerator generator) {
        this.generator = generator;
    }

    /**
     * 设置背景色
     *
     * @param background 背景色
     * @since 4.1.22
     */
    public void setBackground(Color background) {
        this.color = background;
    }

    /**
     * 设置文字透明度
     *
     * @param textAlpha 文字透明度，取值0~1，1表示不透明
     * @since 4.5.17
     */
    public void setTextAlpha(float textAlpha) {
        this.transparency = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, textAlpha);
    }

}
