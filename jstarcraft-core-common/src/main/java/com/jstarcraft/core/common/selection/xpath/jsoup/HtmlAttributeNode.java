package com.jstarcraft.core.common.selection.xpath.jsoup;

import java.util.ArrayList;
import java.util.Collection;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;

/**
 * HTML属性节点
 * 
 * @author Birdy
 *
 */
public class HtmlAttributeNode extends HtmlNode<Attribute> {

    HtmlAttributeNode(HtmlElementNode parent, Attribute value) {
        super(parent, value.getKey(), value);
    }

    public String getProperty() {
        return value.getValue();
    }

    public void setProperty(String property) {
        value.setValue(property);
    }

    static Collection<HtmlAttributeNode> getInstances(HtmlElementNode node, String name) {
        Attributes attributes = node.getValue().attributes();
        ArrayList<HtmlAttributeNode> instances = new ArrayList<>(attributes.size());
        for (Attribute attribute : attributes) {
            if (name.equals(attribute.getKey())) {
                instances.add(new HtmlAttributeNode(node, attribute));
            }
        }
        return instances;
    }

    static Collection<HtmlAttributeNode> getInstances(HtmlElementNode node) {
        Attributes attributes = node.getValue().attributes();
        ArrayList<HtmlAttributeNode> instances = new ArrayList<>(attributes.size());
        for (Attribute attribute : attributes) {
            instances.add(new HtmlAttributeNode(node, attribute));
        }
        return instances;
    }

}
