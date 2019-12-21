package com.jstarcraft.core.common.tuple;

import java.util.Arrays;
import java.util.Iterator;

/**
 * 元组
 * 
 * @author Birdy
 *
 */
public class Tuple implements Iterable<Object> {

    protected Object[] datas;

    protected Iterator<Object> iterator;

    protected Tuple(Object... datas) {
        this.datas = datas;
        this.iterator = Arrays.asList(datas).iterator();
    }

    public Object getData(int index) {
        return datas[index];
    }

    public void setData(int index, Object data) {
        datas[index] = data;
    }

    @Override
    public Iterator<Object> iterator() {
        return iterator;
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
