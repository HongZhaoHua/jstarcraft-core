package com.jstarcraft.core.common.selection.xpath;

import java.io.File;
import java.util.List;

import org.jaxen.Navigator;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.common.selection.xpath.file.FileComponentNode;
import com.jstarcraft.core.common.selection.xpath.file.FileNavigator;
import com.jstarcraft.core.common.selection.xpath.file.FileNode;
import com.jstarcraft.core.common.selection.xpath.file.FileXPath;

public class FileXpathTestCase {

    @Test
    public void testXpath() throws Exception {
        File resources = new File(FileXpathTestCase.class.getClassLoader().getResource(".").toURI());
        Navigator navigator = FileNavigator.getInstance();
        System.out.println(resources.getPath());
        FileComponentNode root = new FileComponentNode(resources);
        // 测试属性节点
        {
            FileXPath xpath = new FileXPath("/@name");
            List<FileNode> nodes = (List) xpath.evaluate(root);
            Assert.assertEquals(1, nodes.size());
        }
        // 测试组件节点
        {
            FileXPath xpath = new FileXPath("//xpath.html");
            List<FileNode> nodes = (List) xpath.evaluate(root);
            Assert.assertEquals(2, nodes.size());
            for (FileNode node : nodes) {
                Assert.assertEquals("xpath.html", node.getName());
            }
        }
        {
            FileXPath xpath = new FileXPath("//xpath");
            List<FileNode> nodes = (List) xpath.evaluate(root);
            Assert.assertEquals(1, nodes.size());
            for (FileNode node : nodes) {
                Assert.assertEquals("xpath", node.getName());
            }
        }
        // 测试轴
        {
            FileXPath xpath = new FileXPath("//*[@name='xpath']/attribute::path");
            List<FileNode> nodes = (List) xpath.evaluate(root);
            Assert.assertEquals(1, nodes.size());
        }
        {
            FileXPath xpath = new FileXPath("//*[@name='xpath']/self::*");
            List<FileNode> nodes = (List) xpath.evaluate(root);
            Assert.assertEquals(1, nodes.size());
        }
        {
            FileXPath xpath = new FileXPath("//*[@name='xpath']/parent::*");
            List<FileNode> nodes = (List) xpath.evaluate(root);
            Assert.assertEquals(1, nodes.size());
        }
        {
            FileXPath xpath = new FileXPath("//*[@name='xpath']/ancestor::*");
            List<FileNode> nodes = (List) xpath.evaluate(root);
            Assert.assertEquals(5, nodes.size());
        }
        {
            FileXPath xpath = new FileXPath("//*[@name='xpath']/child::*[@name='xpath.html']");
            List<FileNode> nodes = (List) xpath.evaluate(root);
            Assert.assertEquals(1, nodes.size());
        }
        {
            FileXPath xpath = new FileXPath("//*[@name='xpath']/descendant::*[@name='xpath.html']");
            List<FileNode> nodes = (List) xpath.evaluate(root);
            Assert.assertEquals(2, nodes.size());
        }
    }

}
