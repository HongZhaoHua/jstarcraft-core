package com.jstarcraft.core.common.selection.xpath.jsoup;

import java.util.Objects;

/**
 * HTML节点
 * 
 * @author Birdy
 *
 * @param <V>
 */
public abstract class HtmlNode<V> {

    /** 双亲 */
    protected HtmlElementNode parent;

    /** 节点名 */
    protected String name;

    /** 节点值 */
    protected V value;

    public HtmlNode(HtmlElementNode parent, String name, V value) {
        this.parent = parent;
        this.name = name;
        this.value = value;
    }

    /**
     * 获取节点双亲
     * 
     * @return
     */
    public final HtmlElementNode getParent() {
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
        HtmlNode that = (HtmlNode) object;
        return Objects.equals(this.value, that.value);
    }

}
