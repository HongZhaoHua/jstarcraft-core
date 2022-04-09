package com.jstarcraft.core.common.selection.xpath.mind;

import java.util.Arrays;
import java.util.Iterator;

import org.jaxen.DefaultNavigator;
import org.jaxen.JaxenConstants;
import org.jaxen.JaxenException;
import org.jaxen.NamedAccessNavigator;
import org.jaxen.Navigator;
import org.jaxen.XPath;

/**
 * 主题浏览器
 * 
 * @author Birdy
 *
 */
public class TopicNavigator extends DefaultNavigator implements NamedAccessNavigator {

    private static final TopicNavigator instance = new TopicNavigator();

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
        return ((TopicNode) object).getName();
    }

    @Override
    public String getElementQName(Object object) {
        return ((TopicNode) object).getName();
    }

    @Override
    public String getAttributeNamespaceUri(Object object) {
        // 文件树不支持命名空间
        return "";
    }

    @Override
    public String getAttributeName(Object object) {
        return ((TopicNode) object).getName();
    }

    @Override
    public String getAttributeQName(Object object) {
        return ((TopicNode) object).getName();
    }

    @Override
    public boolean isDocument(Object object) {
        if (object instanceof TopicComponentNode) {
            TopicComponentNode node = (TopicComponentNode) object;
            return node.getParent() == null;
        } else {
            return false;
        }
    }

    @Override
    public boolean isElement(Object object) {
        if (object instanceof TopicComponentNode) {
            TopicComponentNode node = (TopicComponentNode) object;
            return node.getParent() != null;
        } else {
            return false;
        }
    }

    @Override
    public boolean isAttribute(Object object) {
        if (object instanceof TopicAttributeNode) {
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
        Object value = ((TopicAttributeNode) object).getProperty();
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
        return new TopicXPath(xpath);
    }

    @Override
    public Iterator<TopicNode> getParentAxisIterator(Object contextNode) {
        TopicNode node = ((TopicNode) contextNode);
        if (node.getParent() != null) {
            return Arrays.asList(node.getParent()).iterator();
        } else {
            return JaxenConstants.EMPTY_ITERATOR;
        }
    }

    @Override
    public Iterator<TopicComponentNode> getChildAxisIterator(Object contextNode) {
        TopicNode node = ((TopicNode) contextNode);
        return TopicComponentNode.getInstances(node).iterator();
    }

    @Override
    public Iterator<TopicComponentNode> getChildAxisIterator(Object contextNode, String localName, String namespacePrefix, String namespaceURI) {
        TopicNode node = ((TopicNode) contextNode);
        return TopicComponentNode.getInstances(node, localName).iterator();
    }

    @Override
    public Iterator<TopicAttributeNode> getAttributeAxisIterator(Object contextNode) {
        TopicNode node = ((TopicNode) contextNode);
        return TopicAttributeNode.getInstances(node).iterator();
    }

    @Override
    public Iterator<TopicAttributeNode> getAttributeAxisIterator(Object contextNode, String localName, String namespacePrefix, String namespaceURI) {
        TopicNode node = ((TopicNode) contextNode);
        return Arrays.asList(TopicAttributeNode.getInstance(node, localName)).iterator();
    }

    @Override
    public TopicNode getDocumentNode(Object contextNode) {
        TopicNode node = getParentNode(contextNode);
        while (node != null) {
            contextNode = node;
            node = getParentNode(contextNode);
        }
        return (TopicComponentNode) contextNode;
    }

    @Override
    public TopicNode getParentNode(Object contextNode) {
        return ((TopicNode) contextNode).getParent();
    }

}
