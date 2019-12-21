package com.jstarcraft.core.common.tuple;

/**
 * 五元
 * 
 * @author Birdy
 *
 */
public class Quintet<A, B, C, D, E> extends Quartet<A, B, C, D> {
    
    protected Quintet(Object... datas) {
        super(datas);
    }

    public Quintet(A a, B b, C c, D d, E e) {
        this(new Object[] { a, b, c, d, e });
    }

    public E getE() {
        return (E) datas[4];
    }

    public void setE(E data) {
        datas[4] = data;
    }

}
