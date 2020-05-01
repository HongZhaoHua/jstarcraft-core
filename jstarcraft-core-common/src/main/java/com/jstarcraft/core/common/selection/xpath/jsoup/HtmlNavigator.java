package com.jstarcraft.core.common.selection.xpath.jsoup;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import org.jaxen.DefaultNavigator;
import org.jaxen.JaxenConstants;
import org.jaxen.JaxenException;
import org.jaxen.NamedAccessNavigator;
import org.jaxen.Navigator;
import org.jaxen.XPath;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * HTML浏览器
 * 
 * @author Birdy
 *
 */
public class HtmlNavigator extends DefaultNavigator implements NamedAccessNavigator {

    private static final HtmlNavigator instance = new HtmlNavigator();

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
        return ((Element) object).tagName();
    }

    @Override
    public String getElementQName(Object object) {
        return ((Element) object).tagName();
    }

    @Override
    public String getAttributeNamespaceUri(Object object) {
        // Swing组件树不支持命名空间
        return "";
    }

    @Override
    public String getAttributeName(Object object) {
        return ((Attribute) object).getKey();
    }

    @Override
    public String getAttributeQName(Object object) {
        return ((Attribute) object).getKey();
    }

    @Override
    public boolean isDocument(Object object) {
        if (object instanceof Document) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isElement(Object object) {
        if (object instanceof Element) {
            return !isDocument(object);
        } else {
            return false;
        }
    }

    @Override
    public boolean isAttribute(Object object) {
        if (object instanceof Attribute) {
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
    public String getCommentStringValue(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getElementStringValue(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getAttributeStringValue(Object object) {
        Object value = ((Attribute) object).getValue();
        return value == null ? "" : value.toString();
    }

    @Override
    public String getNamespaceStringValue(Object object) {
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
        return new HtmlXPath(xpath);
    }

    @Override
    public Iterator<Element> getParentAxisIterator(Object contextNode) {
        Element node = ((Element) contextNode);
        if (node.parent() != null) {
            return Arrays.asList(node.parent()).iterator();
        } else {
            return JaxenConstants.EMPTY_ITERATOR;
        }
    }

    @Override
    public Iterator<Element> getChildAxisIterator(Object contextNode) {
        Element node = ((Element) contextNode);
        return node.children().iterator();
    }

    @Override
    public Iterator<Element> getChildAxisIterator(Object contextNode, String localName, String namespacePrefix, String namespaceURI) {
        Element node = ((Element) contextNode);
        LinkedList<Element> children = new LinkedList<>();
        for (Element child : node.children()) {
            if (localName.equals(child.tagName())) {
                children.add(child);
            }
        }
        return children.iterator();
    }

    @Override
    public Iterator<Attribute> getAttributeAxisIterator(Object contextNode) {
        Element node = ((Element) contextNode);
        return node.attributes().iterator();
    }

    @Override
    public Iterator<Attribute> getAttributeAxisIterator(Object contextNode, String localName, String namespacePrefix, String namespaceURI) {
        Element node = ((Element) contextNode);
        LinkedList<Attribute> attributes = new LinkedList<>();
        for (Attribute attribute : node.attributes()) {
            if (localName.equals(attribute.getKey())) {
                attributes.add(attribute);
            }
        }
        return attributes.iterator();
    }

    @Override
    public Element getDocumentNode(Object contextNode) {
        return ((Element) contextNode).root();
    }

    @Override
    public Element getParentNode(Object contextNode) {
        return ((Element) contextNode).parent();
    }

}
