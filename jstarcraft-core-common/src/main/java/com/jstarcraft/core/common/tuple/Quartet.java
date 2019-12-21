package com.jstarcraft.core.common.tuple;

/**
 * 四元
 * 
 * @author Birdy
 *
 */
public class Quartet<A, B, C, D> extends Triplet<A, B, C> {

    protected Quartet(Object... datas) {
        super(datas);
    }

    public Quartet(A a, B b, C c, D d) {
        this(new Object[] { a, b, c, d });
    }

    public D getD() {
        return (D) datas[3];
    }

    public void setD(D data) {
        datas[3] = data;
    }

}
