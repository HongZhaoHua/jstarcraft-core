package com.jstarcraft.core.common.selection.xpath.mind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 主题组件节点
 * 
 * @author Birdy
 *
 */
public class TopicComponentNode extends TopicNode<Topic> {

    public TopicComponentNode(Topic root) {
        super(null, root.getTitle(), root);
    }

    TopicComponentNode(TopicNode parent, Topic value) {
        super(parent, value.getTitle(), value);
    }

    @Override
    Topic getComponent() {
        Topic component = (Topic) getValue();
        return component;
    }

    static Collection<TopicComponentNode> getInstances(TopicNode node, String name) {
        Topic container = node.getComponent();
        List<Topic> components = container.getChildren();
        ArrayList<TopicComponentNode> instances = new ArrayList<>(components.size());
        for (Topic component : components) {
            if (component.getTitle().equals(name)) {
                instances.add(new TopicComponentNode(node, component));
            }
        }
        return instances;
    }

    static Collection<TopicComponentNode> getInstances(TopicNode node) {
        Topic container = node.getComponent();
        List<Topic> components = container.getChildren();
        ArrayList<TopicComponentNode> instances = new ArrayList<>(components.size());
        for (Topic component : components) {
            instances.add(new TopicComponentNode(node, component));
        }
        return instances;
    }

}
