package com.jstarcraft.core.script;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.script.exception.ScriptContextException;

/**
 * 脚本范围
 * 
 * <pre>
 * 每个表达式,函数,线程独立使用各自的脚本范围,保证线程安全
 * </pre>
 * 
 * @author Birdy
 *
 */
public class ScriptScope {

    private HashMap<String, Object> scopeAttributes = new HashMap<>();

    /**
     * 创建属性
     * 
     * @param name
     * @param attribute
     * @return
     */
    public ScriptScope createAttribute(String name, Object attribute) {
        if (scopeAttributes.containsKey(name)) {
            throw new ScriptContextException("脚本范围名称冲突");
        }
        scopeAttributes.put(name, attribute);
        return this;
    }

    /**
     * 创建属性
     * 
     * @param attributes
     * @return
     */
    public ScriptScope createAttributes(Map<String, Object> attributes) {
        for (Entry<String, Object> keyValue : attributes.entrySet()) {
            createAttribute(keyValue.getKey(), keyValue.getValue());
        }
        return this;
    }

    /**
     * 删除属性
     * 
     * @param name
     * @return
     */
    public ScriptScope deleteAttribute(String name) {
        scopeAttributes.remove(name);
        return this;
    }

    /**
     * 删除属性
     * 
     * @return
     */
    public ScriptScope deleteAttributes() {
        scopeAttributes.clear();
        return this;
    }

    /**
     * 获取属性
     * 
     * @return
     */
    Map<String, Object> getAttributes() {
        return scopeAttributes;
    }

    /**
     * 拷贝范围
     * 
     * @return
     */
    ScriptScope copyScope() {
        ScriptScope scope = new ScriptScope();
        scope.scopeAttributes.putAll(scopeAttributes);
        return scope;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        ScriptScope that = (ScriptScope) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.scopeAttributes, that.scopeAttributes);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(scopeAttributes);
        return hash.toHashCode();
    }

}
