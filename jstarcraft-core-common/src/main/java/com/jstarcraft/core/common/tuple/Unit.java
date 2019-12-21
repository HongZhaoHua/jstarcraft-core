package com.jstarcraft.core.common.tuple;

/**
 * 一元
 * 
 * @author Birdy
 *
 */
public class Unit<A> extends Tuple {

    public Unit(Object... datas) {
        if (datas.length != 1) {
            throw new IllegalArgumentException();
        }
        this.datas = datas;
    }

    public A getA() {
        return (A) datas[0];
    }

    public void setA(A data) {
        datas[0] = data;
    }

}
