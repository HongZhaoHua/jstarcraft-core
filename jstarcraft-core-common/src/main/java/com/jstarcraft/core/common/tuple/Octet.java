package com.jstarcraft.core.common.tuple;

/**
 * 八元
 * 
 * @author Birdy
 *
 */
public class Octet<A, B, C, D, E, F, G, H> extends Septet<A, B, C, D, E, F, G> {

    protected Octet(Object... datas) {
        super(datas);
    }

    public Octet(A a, B b, C c, D d, E e, F f, G g, H h) {
        this(new Object[] { a, b, c, d, e, f, g, h });
    }

    public H getH() {
        return (H) datas[7];
    }

    public void setH(H data) {
        datas[7] = data;
    }

}
