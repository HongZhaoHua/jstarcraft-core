package com.jstarcraft.core.common.tuple;

/**
 * 十元
 * 
 * @author Birdy
 *
 */
public class Dectet<A, B, C, D, E, F, G, H, I, J> extends Nonet<A, B, C, D, E, F, G, H, I> {

    protected Dectet(Object... datas) {
        super(datas);
    }

    public Dectet(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j) {
        this(new Object[] { a, b, c, d, e, f, g, h, i, j });
    }

    public J getJ() {
        return (J) datas[9];
    }

    public void setJ(J data) {
        datas[9] = data;
    }

}
