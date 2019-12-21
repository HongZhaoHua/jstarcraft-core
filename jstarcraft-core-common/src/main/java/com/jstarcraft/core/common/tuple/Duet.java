package com.jstarcraft.core.common.tuple;

/**
 * 二元
 * 
 * @author Birdy
 *
 */
public class Duet<A, B> extends Unit<A> {

    public Duet(Object... datas) {
        if (datas.length != 2) {
            throw new IllegalArgumentException();
        }
        this.datas = datas;
    }

    public B getB() {
        return (B) datas[1];
    }
    
    public void setB(B data) {
        datas[1] = data;
    }

}
