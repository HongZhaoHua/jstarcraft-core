package com.jstarcraft.core.common.identification;

/**
 * 标识对象
 * 
 * <pre>
 * 可标识的对象
 * </pre>
 * 
 * @author Birdy
 *
 * @param <K>
 */
public interface IdentityObject<K extends Comparable> extends Comparable<IdentityObject> {

    /**
     * 获取标识
     * 
     * @return
     */
    K getId();

    default int compareTo(IdentityObject that) {
        int compare = this.getClass().getName().compareTo(that.getClass().getName());
        if (compare == 0) {
            return this.getId().compareTo(that.getId());
        } else {
            return compare;
        }
    }

}
