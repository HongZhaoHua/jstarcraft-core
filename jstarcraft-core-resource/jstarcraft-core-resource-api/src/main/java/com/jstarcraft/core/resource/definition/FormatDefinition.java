package com.jstarcraft.core.resource.definition;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.resource.format.FormatAdapter;

/**
 * 格式定义
 * 
 * @author Birdy
 */
public class FormatDefinition {

    /** 适配器 */
    private final FormatAdapter adapter;

    /** 名称 */
    private final String name;

    /** 路径 */
    private final String path;

    /** 后缀 */
    private final String suffix;

    public FormatDefinition(FormatAdapter adapter, String name, String path, String suffix) {
        this.adapter = adapter;
        this.name = name;
        this.path = path;
        this.suffix = suffix;
    }

    public FormatAdapter getAdapter() {
        return adapter;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getSuffix() {
        return suffix;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        FormatDefinition that = (FormatDefinition) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.name, that.name);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(name);
        return hash.toHashCode();
    }

}
