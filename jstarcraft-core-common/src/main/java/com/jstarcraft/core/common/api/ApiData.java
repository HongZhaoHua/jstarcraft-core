package com.jstarcraft.core.common.api;

public interface ApiData<T> {

    /**
     * 检查状态
     * 
     * @return
     */
    boolean checkStatus();

    /**
     * 获取数据
     * 
     * @return
     */
    T getData();

}
