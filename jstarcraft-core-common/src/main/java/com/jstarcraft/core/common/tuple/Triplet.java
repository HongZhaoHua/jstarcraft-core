package com.jstarcraft.core.common.tuple;

/**
 * 三元
 * 
 * @author Birdy
 *
 */
public class Triplet<A, B, C> extends Duet<A, B> {

    protected Triplet(Object... datas) {
        super(datas);
    }

    public Triplet(A a, B b, C c) {
        this(new Object[] { a, b, c });
    }

    public C getC() {
        return (C) datas[2];
    }

    public void setC(C data) {
        datas[2] = data;
    }

}
