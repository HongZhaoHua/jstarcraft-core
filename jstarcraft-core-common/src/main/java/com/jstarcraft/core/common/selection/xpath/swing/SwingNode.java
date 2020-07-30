package com.jstarcraft.core.common.selection.xpath.swing;

import java.awt.Container;
import java.util.Objects;

/**
 * Swing节点
 * 
 * <pre>
 * 代表组件或者属性
 * </pre>
 * 
 * @author Birdy
 *
 * @param <V>
 */
public abstract class SwingNode<V> {

    /** 双亲 */
    protected SwingNode parent;

    /** 节点名 */
    protected String name;

    /** 节点值 */
    protected V value;

    public SwingNode(SwingNode parent, String name, V value) {
        this.parent = parent;
        this.name = name;
        this.value = value;
    }

    /**
     * 获取节点双亲
     * 
     * @return
     */
    public final SwingNode getParent() {
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

    abstract Container getComponent();

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
        SwingNode that = (SwingNode) object;
        return Objects.equals(this.value, that.value);
    }

}
