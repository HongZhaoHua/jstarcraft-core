package com.jstarcraft.core.common.tuple;

/**
 * 一元
 * 
 * @author Birdy
 *
 */
public class Unit<A> extends Tuple {

    protected Unit(Object... datas) {
        super(datas);
    }

    public Unit(A a) {
        this(new Object[] { a });
    }

    public A getA() {
        return (A) datas[0];
    }

    public void setA(A data) {
        datas[0] = data;
    }

}
