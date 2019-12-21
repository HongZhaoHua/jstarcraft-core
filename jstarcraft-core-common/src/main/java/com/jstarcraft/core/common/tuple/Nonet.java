package com.jstarcraft.core.common.tuple;

/**
 * 九元
 * 
 * @author Birdy
 *
 */
public class Nonet<A, B, C, D, E, F, G, H, I> extends Octet<A, B, C, D, E, F, G, H> {

    protected Nonet(Object... datas) {
        super(datas);
    }

    public Nonet(A a, B b, C c, D d, E e, F f, G g, H h, I i) {
        this(new Object[] { a, b, c, d, e, f, g, h, i });
    }

    public I getI() {
        return (I) datas[8];
    }

    public void setI(I data) {
        datas[8] = data;
    }

}
