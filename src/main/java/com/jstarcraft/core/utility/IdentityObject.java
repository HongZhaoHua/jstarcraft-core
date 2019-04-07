package com.jstarcraft.core.utility;

/**
 * 标识对象
 * 
 * <pre>
 * 所有缓存/对象关系映射类型都需要实现此接口
 * </pre>
 * 
 * @author Birdy
 *
 * @param <K>
 */
public interface IdentityObject<K extends Comparable> extends Comparable<IdentityObject> {

	/**
	 * 获取缓存标识
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
