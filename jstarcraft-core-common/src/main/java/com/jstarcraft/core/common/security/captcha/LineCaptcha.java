package com.jstarcraft.core.common.security.captcha;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.concurrent.ThreadLocalRandom;

public class LineCaptcha extends AbstractCaptcha {

    /**
     * 构造，默认5位验证码，150条干扰线
     *
     * @param width  图片宽
     * @param height 图片高
     */
    public LineCaptcha(int width, int height) {
        this(width, height, 5, 150);
    }

    /**
     * 构造
     *
     * @param width     图片宽
     * @param height    图片高
     * @param codeCount 字符个数
     * @param lineCount 干扰线条数
     */
    public LineCaptcha(int width, int height, int count, int interfere) {
        super(width, height, count, interfere);
    }
    // --------------------------------------------------------------------
    // Constructor end

    @Override
    public BufferedImage createImage(String code) {
        // 图像buffer
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = GraphicsUtility.createGraphics(image, this.background == null ? Color.WHITE : this.background);

        // 干扰线
        drawInterfere(g);

        // 字符串
        drawString(g, code);

        return image;
    }

    // -----------------------------------------------------------------------------------------------------
    // Private method start
    /**
     * 绘制字符串
     *
     * @param g    {@link Graphics}画笔
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
     * 绘制干扰线
     *
     * @param g {@link Graphics2D}画笔
     */
    private void drawInterfere(Graphics2D g) {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        // 干扰线
        for (int i = 0; i < this.interfere; i++) {
            int xs = random.nextInt(width);
            int ys = random.nextInt(height);
            int xe = xs + random.nextInt(width / 8);
            int ye = ys + random.nextInt(height / 8);
            g.setColor(GraphicsUtility.randomColor());
            g.drawLine(xs, ys, xe, ye);
        }
    }
}
