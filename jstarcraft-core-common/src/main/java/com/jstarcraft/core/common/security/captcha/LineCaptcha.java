package com.jstarcraft.core.common.security.captcha;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.concurrent.ThreadLocalRandom;

public class LineCaptcha extends AbstractCaptcha {

    /**
     * 构造
     *
     * @param width     图片宽
     * @param height    图片高
     * @param codeCount 字符个数
     * @param lineCount 干扰线条数
     */
    public LineCaptcha(int width, int height, int interfere) {
        super(width, height, interfere);
    }
    // --------------------------------------------------------------------
    // Constructor end

    @Override
    public BufferedImage generateImage(String content) {
        // 图像buffer
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics = GraphicsUtility.createGraphics(image, this.color == null ? Color.WHITE : this.color);

        // 干扰线
        drawInterfere(graphics);

        // 字符串
        drawString(graphics, content);

        return image;
    }

    // -----------------------------------------------------------------------------------------------------
    // Private method start
    /**
     * 绘制字符串
     *
     * @param graphics    {@link Graphics}画笔
     * @param content 验证码
     */
    private void drawString(Graphics2D graphics, String content) {
        // 指定透明度
        if (null != this.transparency) {
            graphics.setComposite(this.transparency);
        }
        GraphicsUtility.drawStringColourful(graphics, content, this.font, this.width, this.height);
    }

    /**
     * 绘制干扰线
     *
     * @param graphics {@link Graphics2D}画笔
     */
    private void drawInterfere(Graphics2D graphics) {
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        // 干扰线
        for (int index = 0; index < this.interfere; index++) {
            int xs = random.nextInt(width);
            int ys = random.nextInt(height);
            int xe = xs + random.nextInt(width / 8);
            int ye = ys + random.nextInt(height / 8);
            graphics.setColor(GraphicsUtility.randomColor());
            graphics.drawLine(xs, ys, xe, ye);
        }
    }
}
