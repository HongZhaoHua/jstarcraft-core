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
    public CircleCaptcha(int width, int height, CodeGenerator generator, int interfere) {
        super(width, height, generator, interfere);
    }

    @Override
    public BufferedImage createImage(String code) {
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = GraphicsUtility.createGraphics(image, this.background == null ? Color.WHITE : this.background);

        // 随机画干扰圈圈
        drawInterfere(g);

        // 画字符串
        drawString(g, code);

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
    private void drawString(Graphics2D g, String code) {
        // 指定透明度
        if (null != this.textAlpha) {
            g.setComposite(this.textAlpha);
        }
        GraphicsUtility.drawStringColourful(g, code, this.font, this.width, this.height);
    }

    /**
     * 画随机干扰
     *
     * @param g {@link Graphics2D}
     */
    private void drawInterfere(Graphics2D g) {
        final ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < this.interfere; i++) {
            g.setColor(GraphicsUtility.randomColor());
            g.drawOval(random.nextInt(width), random.nextInt(height), random.nextInt(height >> 1), random.nextInt(height >> 1));
        }
    }

}
