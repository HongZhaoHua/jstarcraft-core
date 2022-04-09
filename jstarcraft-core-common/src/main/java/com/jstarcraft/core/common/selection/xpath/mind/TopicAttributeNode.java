package com.jstarcraft.core.common.selection.xpath.mind;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.jstarcraft.core.common.reflection.ReflectionUtility;

/**
 * 主题属性节点
 * 
 * @author Birdy
 *
 */
public class TopicAttributeNode extends TopicNode<PropertyDescriptor> {

    private final static Map<String, PropertyDescriptor> properties = ReflectionUtility.getPropertyDescriptors(File.class);

    TopicAttributeNode(TopicNode parent, PropertyDescriptor value) {
        super(parent, value.getName(), value);
    }

    @Override
    Topic getComponent() {
        Topic component = (Topic) getProperty();
        return component;
    }

    public Object getProperty() {
        Topic component = parent.getComponent();
        try {
            return value.getReadMethod().invoke(component);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setProperty(Object property) {
        Topic component = parent.getComponent();
        try {
            value.getWriteMethod().invoke(component, property);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    static TopicAttributeNode getInstance(TopicNode node, String name) {
        Topic container = node.getComponent();
        PropertyDescriptor property = properties.get(name);
        TopicAttributeNode instance = new TopicAttributeNode(node, property);
        return instance;
    }

    static Collection<TopicAttributeNode> getInstances(TopicNode node) {
        Topic container = node.getComponent();
        ArrayList<TopicAttributeNode> instances = new ArrayList<>(properties.size());
        for (PropertyDescriptor property : properties.values()) {
            instances.add(new TopicAttributeNode(node, property));
        }
        return instances;
    }

}
