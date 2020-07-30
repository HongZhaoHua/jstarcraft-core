package com.jstarcraft.core.common.selection.xpath.swing;

import java.util.Arrays;
import java.util.Iterator;

import org.jaxen.DefaultNavigator;
import org.jaxen.JaxenConstants;
import org.jaxen.JaxenException;
import org.jaxen.NamedAccessNavigator;
import org.jaxen.Navigator;
import org.jaxen.XPath;

/**
 * Swing浏览器
 * 
 * @author Birdy
 *
 */
public class SwingNavigator extends DefaultNavigator implements NamedAccessNavigator {

    private static final SwingNavigator instance = new SwingNavigator();

    public static Navigator getInstance() {
        return instance;
    }

    @Override
    public String getElementNamespaceUri(Object object) {
        // Swing组件树不支持命名空间
        return "";
    }

    @Override
    public String getElementName(Object object) {
        return ((SwingNode) object).getName();
    }

    @Override
    public String getElementQName(Object object) {
        return ((SwingNode) object).getName();
    }

    @Override
    public String getAttributeNamespaceUri(Object object) {
        // Swing组件树不支持命名空间
        return "";
    }

    @Override
    public String getAttributeName(Object object) {
        return ((SwingNode) object).getName();
    }

    @Override
    public String getAttributeQName(Object object) {
        return ((SwingNode) object).getName();
    }

    @Override
    public boolean isDocument(Object object) {
        if (object instanceof SwingComponentNode) {
            SwingComponentNode node = (SwingComponentNode) object;
            return node.getParent() == null;
        } else {
            return false;
        }
    }

    @Override
    public boolean isElement(Object object) {
        if (object instanceof SwingComponentNode) {
            SwingComponentNode node = (SwingComponentNode) object;
            return node.getParent() != null;
        } else {
            return false;
        }
    }

    @Override
    public boolean isAttribute(Object object) {
        if (object instanceof SwingAttributeNode) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isNamespace(Object object) {
        // Swing组件树不支持命名空间
        return false;
    }

    @Override
    public boolean isComment(Object object) {
        // Swing组件树不支持注释
        return false;
    }

    @Override
    public boolean isText(Object object) {
        // Swing组件树不支持文本
        return false;
    }

    @Override
    public boolean isProcessingInstruction(Object object) {
        // Swing组件树不支持处理指令
        return false;
    }

    @Override
    public String getElementStringValue(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAttributeStringValue(Object object) {
        Object value = ((SwingAttributeNode) object).getProperty();
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
        return new SwingXPath(xpath);
    }

    @Override
    public Iterator<SwingNode> getParentAxisIterator(Object contextNode) {
        SwingNode node = ((SwingNode) contextNode);
        if (node.getParent() != null) {
            return Arrays.asList(node.getParent()).iterator();
        } else {
            return JaxenConstants.EMPTY_ITERATOR;
        }
    }

    @Override
    public Iterator<SwingComponentNode> getChildAxisIterator(Object contextNode) {
        SwingNode node = ((SwingNode) contextNode);
        return SwingComponentNode.getInstances(node).iterator();
    }

    @Override
    public Iterator<SwingComponentNode> getChildAxisIterator(Object contextNode, String localName, String namespacePrefix, String namespaceURI) {
        SwingNode node = ((SwingNode) contextNode);
        return SwingComponentNode.getInstances(node, localName).iterator();
    }

    @Override
    public Iterator<SwingAttributeNode> getAttributeAxisIterator(Object contextNode) {
        SwingNode node = ((SwingNode) contextNode);
        return SwingAttributeNode.getInstances(node).iterator();
    }

    @Override
    public Iterator<SwingAttributeNode> getAttributeAxisIterator(Object contextNode, String localName, String namespacePrefix, String namespaceURI) {
        SwingNode node = ((SwingNode) contextNode);
        return Arrays.asList(SwingAttributeNode.getInstance(node, localName)).iterator();
    }

    @Override
    public SwingNode getDocumentNode(Object contextNode) {
        SwingNode node = getParentNode(contextNode);
        while (node != null) {
            contextNode = node;
            node = getParentNode(contextNode);
        }
        return (SwingComponentNode) contextNode;
    }

    @Override
    public SwingNode getParentNode(Object contextNode) {
        return ((SwingNode) contextNode).getParent();
    }

}
