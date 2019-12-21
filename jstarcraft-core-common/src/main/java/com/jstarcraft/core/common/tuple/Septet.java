package com.jstarcraft.core.common.tuple;

/**
 * 七元
 * 
 * @author Birdy
 *
 */
public class Septet<A, B, C, D, E, F, G> extends Sextet<A, B, C, D, E, F> {

    protected Septet(Object... datas) {
        super(datas);
    }

    public Septet(A a, B b, C c, D d, E e, F f, G g) {
        this(new Object[] { a, b, c, d, e, f, g });
    }

    public G getG() {
        return (G) datas[6];
    }

    public void setG(G data) {
        datas[6] = data;
    }

}
