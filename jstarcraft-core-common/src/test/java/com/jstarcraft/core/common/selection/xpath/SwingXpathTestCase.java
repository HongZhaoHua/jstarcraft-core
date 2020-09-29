package com.jstarcraft.core.common.selection.xpath;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.jaxen.Navigator;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.common.selection.xpath.swing.SwingAttributeNode;
import com.jstarcraft.core.common.selection.xpath.swing.SwingComponentNode;
import com.jstarcraft.core.common.selection.xpath.swing.SwingNavigator;
import com.jstarcraft.core.common.selection.xpath.swing.SwingNode;
import com.jstarcraft.core.common.selection.xpath.swing.SwingXPath;

public class SwingXpathTestCase {

    @Test
    public void testXpath() throws Exception {
        Navigator navigator = SwingNavigator.getInstance();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container container = frame.getContentPane();
        container.setName("container");
        frame.add(new JLabel("center", SwingConstants.CENTER), BorderLayout.CENTER);
        frame.add(new JLabel("north", SwingConstants.CENTER), BorderLayout.NORTH);
        frame.add(new JLabel("east", SwingConstants.CENTER), BorderLayout.EAST);
        frame.add(new JLabel("west", SwingConstants.CENTER), BorderLayout.WEST);
        frame.add(new JLabel("south", SwingConstants.CENTER), BorderLayout.SOUTH);
        // 适配大小
        frame.pack();
        // 窗体居中
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        EventQueue.invokeAndWait(() -> {
            try {
                SwingComponentNode root = new SwingComponentNode(frame);
                // 测试属性节点
                {
                    SwingXPath xpath = new SwingXPath("/./@visible");
                    List<SwingNode> nodes = (List) xpath.evaluate(root);
                    Assert.assertEquals(1, nodes.size());
                    for (SwingNode node : nodes) {
                        Assert.assertEquals("visible", node.getName());
                    }
                }
                {
                    SwingXPath xpath = new SwingXPath("/./@contentPane/JLabel");
                    List<SwingNode> nodes = (List) xpath.evaluate(root);
                    Assert.assertEquals(5, nodes.size());
                    for (SwingNode node : nodes) {
                        Assert.assertEquals("JLabel", node.getName());
                    }
                }
                {
                    SwingXPath xpath = new SwingXPath("/./@contentPane/@name");
                    List<SwingNode> nodes = (List) xpath.evaluate(root);
                    Assert.assertEquals(1, nodes.size());
                    for (SwingNode node : nodes) {
                        Assert.assertEquals("name", node.getName());
                    }
                }
                {
                    // 设置属性
                    SwingXPath xpath = new SwingXPath("//JLabel/@text");
                    List<SwingAttributeNode> nodes = (List) xpath.evaluate(root);
                    Assert.assertEquals(5, nodes.size());
                    for (SwingAttributeNode node : nodes) {
                        Assert.assertEquals("text", node.getName());
                    }
                    for (SwingAttributeNode node : nodes) {
                        node.setProperty("xpath");
                    }
                    // 获取属性
                    for (SwingAttributeNode node : nodes) {
                        Assert.assertEquals("xpath", node.getProperty());
                    }
                }
                // 测试组件节点
                {
                    SwingXPath xpath = new SwingXPath("/.");
                    List<SwingNode> nodes = (List) xpath.evaluate(root);
                    Assert.assertEquals(1, nodes.size());
                    for (SwingNode node : nodes) {
                        Assert.assertEquals("JFrame", node.getName());
                    }
                }
                {
                    SwingXPath xpath = new SwingXPath("//JPanel/*");
                    List<SwingNode> nodes = (List) xpath.evaluate(root);
                    Assert.assertEquals(5, nodes.size());
                    for (SwingNode node : nodes) {
                        Assert.assertEquals("JLabel", node.getName());
                    }
                }
                {
                    SwingXPath xpath = new SwingXPath("//*[@name='container']");
                    List<SwingNode> nodes = (List) xpath.evaluate(root);
                    Assert.assertEquals(1, nodes.size());
                    for (SwingNode node : nodes) {
                        Assert.assertEquals("JPanel", node.getName());
                    }
                }
                {
                    SwingXPath xpath = new SwingXPath("//*[@visible='true']");
                    List<SwingNode> nodes = (List) xpath.evaluate(root);
                    Assert.assertEquals(8, nodes.size());
                }
                // 测试轴
                {
                    SwingXPath xpath = new SwingXPath("//*[@name='container']/attribute::visible");
                    List<SwingNode> nodes = (List) xpath.evaluate(root);
                    Assert.assertEquals(1, nodes.size());
                }    
                {
                    SwingXPath xpath = new SwingXPath("//*[@name='container']/self::*");
                    List<SwingNode> nodes = (List) xpath.evaluate(root);
                    Assert.assertEquals(1, nodes.size());
                }
                {
                    SwingXPath xpath = new SwingXPath("//*[@name='container']/parent::*");
                    List<SwingNode> nodes = (List) xpath.evaluate(root);
                    Assert.assertEquals(1, nodes.size());
                }
                {
                    SwingXPath xpath = new SwingXPath("//*[@name='container']/ancestor::*");
                    List<SwingNode> nodes = (List) xpath.evaluate(root);
                    Assert.assertEquals(2, nodes.size());
                }
                {
                    SwingXPath xpath = new SwingXPath("//*[@name='container']/child::*");
                    List<SwingNode> nodes = (List) xpath.evaluate(root);
                    Assert.assertEquals(5, nodes.size());
                }
                {
                    SwingXPath xpath = new SwingXPath("//*[@name='container']/descendant::*");
                    List<SwingNode> nodes = (List) xpath.evaluate(root);
                    Assert.assertEquals(5, nodes.size());
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                Assert.fail();
            }
        });
        Thread.sleep(1000);
    }

}
