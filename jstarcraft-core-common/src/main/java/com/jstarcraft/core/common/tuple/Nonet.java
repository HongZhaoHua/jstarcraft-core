package com.jstarcraft.core.common.tuple;

/**
 * 九元
 * 
 * @author Birdy
 *
 */
public class Nonet<A, B, C, D, E, F, G, H, I> extends Octet<A, B, C, D, E, F, G, H> {

    public Nonet(A a, B b, C c, D d, E e, F f, G g, H h, I i) {
        this.datas = new Object[] { a, b, c, d, e, f, g, h, i };
    }

    public Nonet(Object... datas) {
        if (datas.length != 9) {
            throw new IllegalArgumentException();
        }
        this.datas = datas;
    }

    public I getI() {
        return (I) datas[8];
    }

    public void setI(I data) {
        datas[8] = data;
    }

}
