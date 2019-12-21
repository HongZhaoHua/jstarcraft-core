package com.jstarcraft.core.common.tuple;

/**
 * 三元
 * 
 * @author Birdy
 *
 */
public class Triplet<A, B, C> extends Duet<A, B> {

    public Triplet(Object... datas) {
        if (datas.length != 3) {
            throw new IllegalArgumentException();
        }
        this.datas = datas;
    }

    public C getC() {
        return (C) datas[2];
    }

    public void setC(C data) {
        datas[2] = data;
    }

}
