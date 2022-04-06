package com.jstarcraft.core.common.selection.xpath.file;

import java.util.Arrays;
import java.util.Iterator;

import org.jaxen.DefaultNavigator;
import org.jaxen.JaxenConstants;
import org.jaxen.JaxenException;
import org.jaxen.NamedAccessNavigator;
import org.jaxen.Navigator;
import org.jaxen.XPath;

/**
 * 文件浏览器
 * 
 * @author Birdy
 *
 */
public class FileNavigator extends DefaultNavigator implements NamedAccessNavigator {

    private static final FileNavigator instance = new FileNavigator();

    public static Navigator getInstance() {
        return instance;
    }

    @Override
    public String getElementNamespaceUri(Object object) {
        // 文件树不支持命名空间
        return "";
    }

    @Override
    public String getElementName(Object object) {
        return ((FileNode) object).getName();
    }

    @Override
    public String getElementQName(Object object) {
        return ((FileNode) object).getName();
    }

    @Override
    public String getAttributeNamespaceUri(Object object) {
        // 文件树不支持命名空间
        return "";
    }

    @Override
    public String getAttributeName(Object object) {
        return ((FileNode) object).getName();
    }

    @Override
    public String getAttributeQName(Object object) {
        return ((FileNode) object).getName();
    }

    @Override
    public boolean isDocument(Object object) {
        if (object instanceof FileComponentNode) {
            FileComponentNode node = (FileComponentNode) object;
            return node.getParent() == null;
        } else {
            return false;
        }
    }

    @Override
    public boolean isElement(Object object) {
        if (object instanceof FileComponentNode) {
            FileComponentNode node = (FileComponentNode) object;
            return node.getParent() != null;
        } else {
            return false;
        }
    }

    @Override
    public boolean isAttribute(Object object) {
        if (object instanceof FileAttributeNode) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isNamespace(Object object) {
        // 文件树不支持命名空间
        return false;
    }

    @Override
    public boolean isComment(Object object) {
        // 文件树不支持注释
        return false;
    }

    @Override
    public boolean isText(Object object) {
        // 文件树不支持文本
        return false;
    }

    @Override
    public boolean isProcessingInstruction(Object object) {
        // 文件树不支持处理指令
        return false;
    }

    @Override
    public String getElementStringValue(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAttributeStringValue(Object object) {
        Object value = ((FileAttributeNode) object).getProperty();
        return value == null ? "" : value.toString();
    }

    @Override
    public String getNamespaceStringValue(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCommentStringValue(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTextStringValue(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getNamespacePrefix(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XPath parseXPath(String xpath) throws JaxenException {
        return new FileXPath(xpath);
    }

    @Override
    public Iterator<FileNode> getParentAxisIterator(Object contextNode) {
        FileNode node = ((FileNode) contextNode);
        if (node.getParent() != null) {
            return Arrays.asList(node.getParent()).iterator();
        } else {
            return JaxenConstants.EMPTY_ITERATOR;
        }
    }

    @Override
    public Iterator<FileComponentNode> getChildAxisIterator(Object contextNode) {
        FileNode node = ((FileNode) contextNode);
        return FileComponentNode.getInstances(node).iterator();
    }

    @Override
    public Iterator<FileComponentNode> getChildAxisIterator(Object contextNode, String localName, String namespacePrefix, String namespaceURI) {
        FileNode node = ((FileNode) contextNode);
        return FileComponentNode.getInstances(node, localName).iterator();
    }

    @Override
    public Iterator<FileAttributeNode> getAttributeAxisIterator(Object contextNode) {
        FileNode node = ((FileNode) contextNode);
        return FileAttributeNode.getInstances(node).iterator();
    }

    @Override
    public Iterator<FileAttributeNode> getAttributeAxisIterator(Object contextNode, String localName, String namespacePrefix, String namespaceURI) {
        FileNode node = ((FileNode) contextNode);
        return Arrays.asList(FileAttributeNode.getInstance(node, localName)).iterator();
    }

    @Override
    public FileNode getDocumentNode(Object contextNode) {
        FileNode node = getParentNode(contextNode);
        while (node != null) {
            contextNode = node;
            node = getParentNode(contextNode);
        }
        return (FileComponentNode) contextNode;
    }

    @Override
    public FileNode getParentNode(Object contextNode) {
        return ((FileNode) contextNode).getParent();
    }

}
