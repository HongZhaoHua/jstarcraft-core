package com.jstarcraft.core.storage;

public enum ConditionType {

    /** all */
    All(0, 1),

    /** from <= x <= to */
    Between(2, 3),

    /** x == value */
    Equal(1, 2),

    /** x > from */
    Higher(1, 2),

    /** x in values */
    In(1, Integer.MAX_VALUE),

    /** x < to */
    Lower(1, 2),

    /** x != value */
    Unequal(1, 2);

    /** 最小数量(包含) */
    private final int minimum;

    /** 最大数量(不包含) */
    private final int maximum;

    private ConditionType(int minimum, int maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public <I> boolean check(I... values) {
        return values.length >= minimum && values.length < maximum;
    }

}
