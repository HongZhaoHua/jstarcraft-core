package com.jstarcraft.core.common.selection.xpath.jsoup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jsoup.nodes.Node;

/**
 * HTML元素节点
 * 
 * @author Birdy
 *
 */
public class HtmlElementNode extends HtmlNode<Node> {

    public HtmlElementNode(Node root) {
        super(null, root.nodeName(), root);
    }

    HtmlElementNode(HtmlElementNode parent, Node value) {
        super(parent, value.nodeName(), value);
    }

    static Collection<HtmlElementNode> getInstances(HtmlElementNode node, String name) {
        List<Node> elements = node.getValue().childNodes();
        ArrayList<HtmlElementNode> instances = new ArrayList<>(elements.size());
        for (Node element : elements) {
            if (name.equals(element.nodeName())) {
                instances.add(new HtmlElementNode(node, element));
            }
        }
        return instances;
    }

    static Collection<HtmlElementNode> getInstances(HtmlElementNode node) {
        List<Node> elements = node.getValue().childNodes();
        ArrayList<HtmlElementNode> instances = new ArrayList<>(elements.size());
        for (Node element : elements) {
            instances.add(new HtmlElementNode(node, element));
        }
        return instances;
    }

}
