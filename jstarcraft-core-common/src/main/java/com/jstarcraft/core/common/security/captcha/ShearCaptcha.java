package com.jstarcraft.core.common.security.captcha;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.jstarcraft.core.utility.RandomUtility;

public class ShearCaptcha extends AbstractCaptcha {

    /**
     * 构造
     *
     * @param width     图片宽
     * @param height    图片高
     * @param codeCount 字符个数
     * @param thickness 干扰线宽度
     */
    public ShearCaptcha(int width, int height, int interfere) {
        super(width, height, interfere);
    }

    @Override
    public BufferedImage generateImage(String content) {
        final BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics = GraphicsUtility.createGraphics(image, this.color == null ? Color.WHITE : this.color);

        // 画字符串
        drawString(graphics, content);

        // 扭曲
        shear(graphics, this.width, this.height, this.color == null ? Color.WHITE : this.color);
        // 画干扰线
        drawInterfere(graphics, 0, RandomUtility.randomInteger(this.height) + 1, this.width, RandomUtility.randomInteger(this.height) + 1, this.interfere, GraphicsUtility.randomColor());

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
     * 扭曲
     *
     * @param graphics     {@link Graphics}
     * @param w1    w1
     * @param h1    h1
     * @param color 颜色
     */
    private void shear(Graphics graphics, int w1, int h1, Color color) {
        shearX(graphics, w1, h1, color);
        shearY(graphics, w1, h1, color);
    }

    /**
     * X坐标扭曲
     *
     * @param graphics     {@link Graphics}
     * @param w1    宽
     * @param h1    高
     * @param color 颜色
     */
    private void shearX(Graphics graphics, int w1, int h1, Color color) {

        int period = RandomUtility.randomInteger(this.width);

        int frames = 1;
        int phase = RandomUtility.randomInteger(2);

        for (int index = 0; index < h1; index++) {
            double d = (double) (period >> 1) * Math.sin((double) index / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            graphics.copyArea(0, index, w1, 1, (int) d, 0);
            graphics.setColor(color);
            graphics.drawLine((int) d, index, 0, index);
            graphics.drawLine((int) d + w1, index, w1, index);
        }

    }

    /**
     * Y坐标扭曲
     *
     * @param graphics     {@link Graphics}
     * @param w1    宽
     * @param h1    高
     * @param color 颜色
     */
    private void shearY(Graphics graphics, int w1, int h1, Color color) {

        int period = RandomUtility.randomInteger(this.height >> 1);

        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            graphics.copyArea(i, 0, 1, h1, 0, (int) d);
            graphics.setColor(color);
            // 擦除原位置的痕迹
            graphics.drawLine(i, (int) d, i, 0);
            graphics.drawLine(i, (int) d + h1, i, h1);
        }

    }

    /**
     * 干扰线
     *
     * @param graphics         {@link Graphics}
     * @param x1        x1
     * @param y1        y1
     * @param x2        x2
     * @param y2        y2
     * @param thickness 粗细
     * @param c         颜色
     */
    @SuppressWarnings("SameParameterValue")
    private void drawInterfere(Graphics graphics, int x1, int y1, int x2, int y2, int thickness, Color c) {

        // The thick line is in fact a filled polygon
        graphics.setColor(c);
        int dX = x2 - x1;
        int dY = y2 - y1;
        // line length
        double lineLength = Math.sqrt(dX * dX + dY * dY);

        double scale = (double) (thickness) / (2 * lineLength);

        // The x and y increments from an endpoint needed to create a
        // rectangle...
        double ddx = -scale * (double) dY;
        double ddy = scale * (double) dX;
        ddx += (ddx > 0) ? 0.5 : -0.5;
        ddy += (ddy > 0) ? 0.5 : -0.5;
        int dx = (int) ddx;
        int dy = (int) ddy;

        // Now we can compute the corner points...
        int[] xPoints = new int[4];
        int[] yPoints = new int[4];

        xPoints[0] = x1 + dx;
        yPoints[0] = y1 + dy;
        xPoints[1] = x1 - dx;
        yPoints[1] = y1 - dy;
        xPoints[2] = x2 - dx;
        yPoints[2] = y2 - dy;
        xPoints[3] = x2 + dx;
        yPoints[3] = y2 + dy;

        graphics.fillPolygon(xPoints, yPoints, 4);
    }

}
