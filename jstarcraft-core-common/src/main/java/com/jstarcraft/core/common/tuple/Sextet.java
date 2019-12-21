package com.jstarcraft.core.common.tuple;

/**
 * 六元
 * 
 * @author Birdy
 *
 */
public class Sextet<A, B, C, D, E, F> extends Quintet<A, B, C, D, E> {

    protected Sextet(Object... datas) {
        super(datas);
    }

    public Sextet(A a, B b, C c, D d, E e, F f) {
        this(new Object[] { a, b, c, d, e, f });
    }

    public F getF() {
        return (F) datas[5];
    }

    public void setF(F data) {
        datas[5] = data;
    }

}
