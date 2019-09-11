package com.jstarcraft.core.common.conversion.csv.query;

import com.jstarcraft.core.utility.StringUtility;

/**
 * AWK复杂条件
 * 
 * @author Birdy
 *
 */
public class ComplexCondition implements AwkCondition {

    private final AwkOperator operator;
    private final AwkCondition left;
    private final AwkCondition right;

    ComplexCondition(AwkOperator operator, AwkCondition left, AwkCondition right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public String getBeginContent() {
        return left.getBeginContent() + right.getBeginContent();
    }

    @Override
    public String getEndContent() {
        return left.getEndContent() + right.getEndContent();
    }

    @Override
    public String toString() {
        return "(" + left + StringUtility.SPACE + operator.getOperate() + StringUtility.SPACE + right + ")";
    }

}
