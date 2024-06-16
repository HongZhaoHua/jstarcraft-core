package com.jstarcraft.core.common.security.captcha;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.concurrent.ThreadLocalRandom;

public class CircleCaptcha extends AbstractCaptcha {

    /**
     * 构造
     *
     * @param width          图片宽
     * @param height         图片高
     * @param codeCount      字符个数
     * @param interfereCount 验证码干扰元素个数
     */
    public CircleCaptcha(int width, int height, int interfere) {
        super(width, height, interfere);
    }

    @Override
    public BufferedImage generateImage(String content) {
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics = GraphicsUtility.createGraphics(image, this.color == null ? Color.WHITE : this.color);

        // 随机画干扰圈圈
        drawInterfere(graphics);

        // 画字符串
        drawString(graphics, content);

        return image;
    }

    // -----------------------------------------------------------------------------------------------------
    // Private method start
    /**
     * 绘制字符串
     *
     * @param g    {@link Graphics2D}画笔
     * @param code 验证码
     */
    private void drawString(Graphics2D graphics, String content) {
        // 指定透明度
        if (null != this.transparency) {
            graphics.setComposite(this.transparency);
        }
        GraphicsUtility.drawStringColourful(graphics, content, this.font, this.width, this.height);
    }

    /**
     * 画随机干扰
     *
     * @param g {@link Graphics2D}
     */
    private void drawInterfere(Graphics2D graphics) {
        final ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int index = 0; index < this.interfere; index++) {
            graphics.setColor(GraphicsUtility.randomColor());
            graphics.drawOval(random.nextInt(width), random.nextInt(height), random.nextInt(height >> 1), random.nextInt(height >> 1));
        }
    }

}
