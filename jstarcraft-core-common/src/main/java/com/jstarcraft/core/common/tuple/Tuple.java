package com.jstarcraft.core.common.tuple;

/**
 * 元组
 * 
 * @author Birdy
 *
 */
public class Tuple {

    protected Object[] datas;

    protected Tuple(Object... datas) {
        this.datas = datas;
    }

    public Object getData(int index) {
        return datas[index];
    }

    public void setData(int index, Object data) {
        datas[index] = data;
    }

}
