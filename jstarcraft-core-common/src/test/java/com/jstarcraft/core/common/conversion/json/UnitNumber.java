package com.jstarcraft.core.common.conversion.json;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.utility.StringUtility;

public class UnitNumber implements Comparable<UnitNumber> {

    public final static int KILO = 1000;

    public final static UnitNumber ZERO = new UnitNumber();

    /** 单位(1代表千位,2代表百万位,3代表十亿位,以此类推) */
    private int unit;

    /** 值 */
    private double value;

    UnitNumber() {
    }

    public UnitNumber(int unit, double value) {
        this.unit = unit;
        this.value = value;
    }

    public int getUnit() {
        return unit;
    }

    public double getValue() {
        return value;
    }

    @Override
    public int compareTo(UnitNumber that) {
        if (this == that) {
            return 0;
        }
        int unit = this.unit > that.unit ? this.unit : that.unit;
        double leftValue = this.value;
        double rightValue = that.value;

        leftValue = leftValue * Math.pow(UnitNumber.KILO, (this.unit - unit));
        rightValue = rightValue * Math.pow(UnitNumber.KILO, (that.unit - unit));

        if (leftValue < rightValue) {
            return -1;
        } else if (leftValue > rightValue) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        UnitNumber that = (UnitNumber) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.unit, that.unit);
        equal.append(this.value, that.value);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(unit);
        hash.append(value);
        return hash.toHashCode();
    }

    @Override
    public String toString() {
        return StringUtility.reflect(this);
    }

    public static UnitNumber instaceOfAdd(int unit, UnitNumber left, UnitNumber right) {
        int leftUnit = left.unit;
        double leftValue = left.value;
        int rightUnit = right.unit;
        double rightValue = right.value;

        // 加法必须在相同基准运算
        leftValue = leftValue * Math.pow(KILO, (leftUnit - unit));
        rightValue = rightValue * Math.pow(KILO, (rightUnit - unit));

        double value = leftValue + rightValue;
        UnitNumber instance = new UnitNumber(unit, value);
        return instance;
    }

    public static UnitNumber instaceOfSubtract(int unit, UnitNumber left, UnitNumber right) {
        int leftUnit = left.unit;
        double leftValue = left.value;
        int rightUnit = right.unit;
        double rightValue = right.value;

        // 减法必须在相同基准运算
        leftValue = leftValue * Math.pow(KILO, (leftUnit - unit));
        rightValue = rightValue * Math.pow(KILO, (rightUnit - unit));

        double value = leftValue - rightValue;
        UnitNumber instance = new UnitNumber(unit, value);
        return instance;
    }

    public static UnitNumber instaceOfMultiply(int unit, UnitNumber left, UnitNumber right) {
        int leftUnit = left.unit;
        double leftValue = left.value;
        int rightUnit = right.unit;
        double rightValue = right.value;

        double value = leftValue * rightValue;
        value = value * Math.pow(KILO, (leftUnit + rightUnit - unit));

        UnitNumber instance = new UnitNumber(unit, value);
        return instance;
    }

    public static UnitNumber instaceOfDivide(int unit, UnitNumber left, UnitNumber right) {
        int leftUnit = left.unit;
        double leftValue = left.value;
        int rightUnit = right.unit;
        double rightValue = right.value;

        double value = leftValue / rightValue;
        value = value * Math.pow(KILO, (leftUnit + rightUnit - unit));

        UnitNumber instance = new UnitNumber(unit, value);
        return instance;
    }

}
