package com.jstarcraft.core.common.tuple;

/**
 * 五元
 * 
 * @author Birdy
 *
 */
public class Quintet<A, B, C, D, E> extends Quartet<A, B, C, D> {

    public Quintet(Object... datas) {
        if (datas.length != 5) {
            throw new IllegalArgumentException();
        }
        this.datas = datas;
    }

    public E getE() {
        return (E) datas[4];
    }
    
    public void setE(E data) {
        datas[4] = data;
    }

}
