package com.jstarcraft.core.common.selection.xpath.file;

import java.io.File;
import java.util.Objects;

/**
 * 文件节点
 * 
 * <pre>
 * 代表目录,文件或者属性
 * </pre>
 * 
 * @author Birdy
 *
 * @param <V>
 */
public abstract class FileNode<V> {

    /** 双亲 */
    protected FileNode parent;

    /** 节点名 */
    protected String name;

    /** 节点值 */
    protected V value;

    public FileNode(FileNode parent, String name, V value) {
        this.parent = parent;
        this.name = name;
        this.value = value;
    }

    /**
     * 获取节点双亲
     * 
     * @return
     */
    public final FileNode getParent() {
        return this.parent;
    }

    /**
     * 获取节点名
     * 
     * @return
     */
    public final String getName() {
        return name;
    }

    /**
     * 获取节点值
     * 
     * @return
     */
    public final V getValue() {
        return value;
    }

    abstract File getComponent();

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        FileNode that = (FileNode) object;
        return Objects.equals(this.value, that.value);
    }

}
