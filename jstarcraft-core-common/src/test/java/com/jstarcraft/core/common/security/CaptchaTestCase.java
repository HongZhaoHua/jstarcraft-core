package com.jstarcraft.core.common.security;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.junit.Test;

import com.jstarcraft.core.common.security.captcha.Captcha;
import com.jstarcraft.core.common.security.captcha.CircleCaptcha;
import com.jstarcraft.core.common.security.captcha.CodeGenerator;
import com.jstarcraft.core.common.security.captcha.MathGenerator;

public class CaptchaTestCase {

    @Test
    public void testCaptcha() throws Exception {
        JFrame frame = new JFrame("testCaptcha");
        frame.setPreferredSize(new Dimension(1000, 1000));
        // 适配大小
        frame.pack();
        // 窗体居中
        frame.setLocationRelativeTo(null);
        CodeGenerator generator = new MathGenerator(2);
        Captcha captcha = new CircleCaptcha(300, 100, generator, 5);
        BufferedImage image = captcha.createCode();
        JLabel label = new JLabel(new ImageIcon(image));
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.setVisible(true);
        Thread.sleep(5000);
    }

}
