package com.jstarcraft.core.common.selection.xpath.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 文件组件节点
 * 
 * @author Birdy
 *
 */
public class FileComponentNode extends FileNode<File> {

    public FileComponentNode(File root) {
        super(null, root.getName(), root);
    }

    FileComponentNode(FileNode parent, File value) {
        super(parent, value.getName(), value);
    }

    @Override
    File getComponent() {
        File component = (File) getValue();
        return component;
    }

    static Collection<FileComponentNode> getInstances(FileNode node, String name) {
        File container = node.getComponent();
        File[] components = container.isDirectory() ? container.listFiles((directory, file) -> {
            return file.equals(name);
        }) : new File[0];
        ArrayList<FileComponentNode> instances = new ArrayList<>(components.length);
        for (File component : components) {
            instances.add(new FileComponentNode(node, component));
        }
        return instances;
    }

    static Collection<FileComponentNode> getInstances(FileNode node) {
        File container = node.getComponent();
        File[] components = container.isDirectory() ? container.listFiles() : new File[0];
        ArrayList<FileComponentNode> instances = new ArrayList<>(components.length);
        for (File component : components) {
            instances.add(new FileComponentNode(node, component));
        }
        return instances;
    }

}
