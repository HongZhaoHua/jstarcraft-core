package com.jstarcraft.core.common.selection.xpath.swing;

import java.awt.Container;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.jstarcraft.core.common.reflection.ReflectionUtility;

/**
 * Swing属性节点
 * 
 * @author Birdy
 *
 */
public class SwingAttributeNode extends SwingNode<PropertyDescriptor> {

    SwingAttributeNode(SwingNode parent, PropertyDescriptor value) {
        super(parent, value.getName(), value);
    }

    @Override
    Container getComponent() {
        Container component = (Container) getProperty();
        return component;
    }

    public Object getProperty() {
        Container component = parent.getComponent();
        try {
            return value.getReadMethod().invoke(component);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setProperty(Object property) {
        Container component = parent.getComponent();
        try {
            value.getWriteMethod().invoke(component, property);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    static SwingAttributeNode getInstance(SwingNode node, String name) {
        Container container = node.getComponent();
        Map<String, PropertyDescriptor> properties = ReflectionUtility.getPropertyDescriptors(container.getClass());
        PropertyDescriptor property = properties.get(name);
        SwingAttributeNode instance = new SwingAttributeNode(node, property);
        return instance;
    }

    static Collection<SwingAttributeNode> getInstances(SwingNode node) {
        Container container = node.getComponent();
        Map<String, PropertyDescriptor> properties = ReflectionUtility.getPropertyDescriptors(container.getClass());
        ArrayList<SwingAttributeNode> instances = new ArrayList<>(properties.size());
        for (PropertyDescriptor property : properties.values()) {
            instances.add(new SwingAttributeNode(node, property));
        }
        return instances;
    }

}
