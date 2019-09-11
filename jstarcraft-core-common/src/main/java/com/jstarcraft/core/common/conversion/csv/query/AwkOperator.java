package com.jstarcraft.core.common.conversion.csv.query;

/**
 * AWK运算符
 * 
 * @author Birdy
 */
public enum AwkOperator {

    /** && */
    AND("&&"),
    /** == */
    EQUAL("=="),
    /** >= */
    GREATER_EQUAL(">="),
    /** > */
    GREATER_THAN(">"),
    /** in */
    IN("in"),
    /** <= */
    LESS_EQUAL("<="),
    /** < */
    LESS_THAN("<"),
    /** != */
    NOT("!="),
    /** || */
    OR("||");

    private final String operate;

    private AwkOperator(String operate) {
        this.operate = operate;
    }

    public String getOperate() {
        return operate;
    }
}