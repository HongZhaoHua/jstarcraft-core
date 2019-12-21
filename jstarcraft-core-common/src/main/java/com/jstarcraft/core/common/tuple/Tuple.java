package com.jstarcraft.core.common.tuple;

import java.util.Arrays;

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

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        Tuple that = (Tuple) object;
        if (!Arrays.equals(this.datas, that.datas))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 1;
        hash = prime * hash + Arrays.hashCode(datas);
        return hash;
    }

}
