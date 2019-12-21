package com.jstarcraft.core.common.tuple;

/**
 * 四元
 * 
 * @author Birdy
 *
 */
public class Quartet<A, B, C, D> extends Triplet<A, B, C> {

    public Quartet(Object... datas) {
        if (datas.length != 4) {
            throw new IllegalArgumentException();
        }
        this.datas = datas;
    }

    public D getD() {
        return (D) datas[3];
    }
    
    public void setD(D data) {
        datas[3] = data;
    }

}
