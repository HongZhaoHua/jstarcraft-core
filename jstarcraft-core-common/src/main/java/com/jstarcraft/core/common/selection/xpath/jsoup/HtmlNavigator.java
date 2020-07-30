package com.jstarcraft.core.common.selection.xpath.jsoup;

import java.util.Arrays;
import java.util.Iterator;

import org.jaxen.DefaultNavigator;
import org.jaxen.JaxenConstants;
import org.jaxen.JaxenException;
import org.jaxen.NamedAccessNavigator;
import org.jaxen.Navigator;
import org.jaxen.XPath;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

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
        // HTML不支持命名空间
        return "";
    }

    @Override
    public String getElementName(Object object) {
        return ((HtmlNode) object).getName();
    }

    @Override
    public String getElementQName(Object object) {
        return ((HtmlNode) object).getName();
    }

    @Override
    public String getAttributeNamespaceUri(Object object) {
        // HTML不支持命名空间
        return "";
    }

    @Override
    public String getAttributeName(Object object) {
        return ((HtmlNode) object).getName();
    }

    @Override
    public String getAttributeQName(Object object) {
        return ((HtmlNode) object).getName();
    }

    @Override
    public boolean isDocument(Object object) {
        if (object instanceof HtmlNode) {
            object = ((HtmlNode) object).getValue();
            if (object instanceof Document) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isElement(Object object) {
        if (object instanceof HtmlNode) {
            object = ((HtmlNode) object).getValue();
            if (object instanceof Element) {
                return !isDocument(object);
            }
        }
        return false;
    }

    @Override
    public boolean isAttribute(Object object) {
        if (object instanceof HtmlNode) {
            object = ((HtmlNode) object).getValue();
            if (object instanceof Attribute) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isNamespace(Object object) {
        // HTML不支持命名空间
        return false;
    }

    @Override
    public boolean isComment(Object object) {
        if (object instanceof HtmlNode) {
            object = ((HtmlNode) object).getValue();
            if (object instanceof Comment) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isText(Object object) {
        if (object instanceof HtmlNode) {
            object = ((HtmlNode) object).getValue();
            if (object instanceof TextNode) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isProcessingInstruction(Object object) {
        // HTML不支持处理指令
        return false;
    }

    @Override
    public String getElementStringValue(Object object) {
        Element element = (Element) ((HtmlNode) object).getValue();
        String value = element.html();
        return value;
    }

    @Override
    public String getAttributeStringValue(Object object) {
        Attribute attribute = (Attribute) ((HtmlNode) object).getValue();
        String value = attribute.getValue();
        return value;
    }

    @Override
    public String getNamespaceStringValue(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCommentStringValue(Object object) {
        Comment comment = (Comment) ((HtmlNode) object).getValue();
        String value = comment.getData();
        return value;
    }

    @Override
    public String getTextStringValue(Object object) {
        TextNode text = (TextNode) ((HtmlNode) object).getValue();
        String value = text.text();
        return value;
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
    public Iterator<HtmlElementNode> getParentAxisIterator(Object contextNode) {
        HtmlNode node = ((HtmlNode) contextNode);
        if (node.getParent() != null) {
            return Arrays.asList(node.getParent()).iterator();
        } else {
            return JaxenConstants.EMPTY_ITERATOR;
        }
    }

    @Override
    public Iterator<HtmlElementNode> getChildAxisIterator(Object contextNode) {
        HtmlElementNode node = ((HtmlElementNode) contextNode);
        return HtmlElementNode.getInstances(node).iterator();
    }

    @Override
    public Iterator<HtmlElementNode> getChildAxisIterator(Object contextNode, String localName, String namespacePrefix, String namespaceURI) {
        HtmlElementNode node = ((HtmlElementNode) contextNode);
        return HtmlElementNode.getInstances(node, localName).iterator();
    }

    @Override
    public Iterator<HtmlAttributeNode> getAttributeAxisIterator(Object contextNode) {
        HtmlElementNode node = ((HtmlElementNode) contextNode);
        return HtmlAttributeNode.getInstances(node).iterator();
    }

    @Override
    public Iterator<HtmlAttributeNode> getAttributeAxisIterator(Object contextNode, String localName, String namespacePrefix, String namespaceURI) {
        HtmlElementNode node = ((HtmlElementNode) contextNode);
        return HtmlAttributeNode.getInstances(node, localName).iterator();
    }

    @Override
    public HtmlElementNode getDocumentNode(Object contextNode) {
        HtmlElementNode node = getParentNode(contextNode);
        while (node != null) {
            contextNode = node;
            node = getParentNode(contextNode);
        }
        return (HtmlElementNode) contextNode;
    }

    @Override
    public HtmlElementNode getParentNode(Object contextNode) {
        return ((HtmlNode) contextNode).getParent();
    }

}
