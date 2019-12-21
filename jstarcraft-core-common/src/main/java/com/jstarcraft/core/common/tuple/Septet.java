package com.jstarcraft.core.common.tuple;

/**
 * 七元
 * 
 * @author Birdy
 *
 */
public class Septet<A, B, C, D, E, F, G> extends Sextet<A, B, C, D, E, F> {

    public Septet(Object... datas) {
        if (datas.length != 7) {
            throw new IllegalArgumentException();
        }
        this.datas = datas;
    }

    public G getG() {
        return (G) datas[6];
    }
    
    public void setG(G data) {
        datas[6] = data;
    }

}
