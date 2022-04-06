package com.jstarcraft.core.common.selection.xpath.file;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.jstarcraft.core.common.reflection.ReflectionUtility;

/**
 * 文件属性节点
 * 
 * @author Birdy
 *
 */
public class FileAttributeNode extends FileNode<PropertyDescriptor> {

    private final static Map<String, PropertyDescriptor> properties = ReflectionUtility.getPropertyDescriptors(File.class);

    FileAttributeNode(FileNode parent, PropertyDescriptor value) {
        super(parent, value.getName(), value);
    }

    @Override
    File getComponent() {
        File component = (File) getProperty();
        return component;
    }

    public Object getProperty() {
        File component = parent.getComponent();
        try {
            return value.getReadMethod().invoke(component);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setProperty(Object property) {
        File component = parent.getComponent();
        try {
            value.getWriteMethod().invoke(component, property);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    static FileAttributeNode getInstance(FileNode node, String name) {
        File container = node.getComponent();
        PropertyDescriptor property = properties.get(name);
        FileAttributeNode instance = new FileAttributeNode(node, property);
        return instance;
    }

    static Collection<FileAttributeNode> getInstances(FileNode node) {
        File container = node.getComponent();
        ArrayList<FileAttributeNode> instances = new ArrayList<>(properties.size());
        for (PropertyDescriptor property : properties.values()) {
            instances.add(new FileAttributeNode(node, property));
        }
        return instances;
    }

}
