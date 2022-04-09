package com.jstarcraft.core.common.selection.xpath.mind;

import java.util.Objects;

/**
 * 主题节点
 * 
 * <pre>
 * 代表主题或者属性
 * </pre>
 * 
 * @author Birdy
 *
 * @param <V>
 */
public abstract class TopicNode<V> {

    /** 双亲 */
    protected TopicNode parent;

    /** 节点名 */
    protected String name;

    /** 节点值 */
    protected V value;

    public TopicNode(TopicNode parent, String name, V value) {
        this.parent = parent;
        this.name = name;
        this.value = value;
    }

    /**
     * 获取节点双亲
     * 
     * @return
     */
    public final TopicNode getParent() {
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

    abstract Topic getComponent();

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
        TopicNode that = (TopicNode) object;
        return Objects.equals(this.value, that.value);
    }

}
