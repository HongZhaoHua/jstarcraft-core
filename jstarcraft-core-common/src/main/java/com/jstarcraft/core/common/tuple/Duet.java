package com.jstarcraft.core.common.tuple;

/**
 * 二元
 * 
 * @author Birdy
 *
 */
public class Duet<A, B> extends Unit<A> {

    protected Duet(Object... datas) {
        super(datas);
    }

    public Duet(A a, B b) {
        this(new Object[] { a, b });
    }

    public B getB() {
        return (B) datas[1];
    }

    public void setB(B data) {
        datas[1] = data;
    }

}
