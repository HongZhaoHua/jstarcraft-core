package com.jstarcraft.core.common.hash;

/**
 * 哈希函数
 * 
 * @author Birdy
 *
 */
public interface HashFunction<T> {

    int hash(T data);

}
