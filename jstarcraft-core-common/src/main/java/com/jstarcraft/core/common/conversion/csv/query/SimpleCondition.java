package com.jstarcraft.core.common.conversion.csv.query;

import java.lang.reflect.Array;

import org.apache.commons.lang3.SystemUtils;

import com.jstarcraft.core.utility.StringUtility;

/**
 * AWK简单条件
 * 
 * @author Birdy
 *
 */
public class SimpleCondition implements AwkCondition {

    /** Linux操作系统在处理双引号字符串的时候,美元符号需要转义 */
    private static final String prefix = SystemUtils.IS_OS_WINDOWS ? "$" : "\\$";

    private final int index;
    private final AwkOperator operator;
    private final String property;
    private final Object value;

    SimpleCondition(AwkOperator operator, int index, String property, Object value) {
        this.index = index;
        this.operator = operator;
        this.property = property;
        this.value = value;
    }

    @Override
    public String getBeginContent() {
        if (AwkOperator.IN.equals(operator)) {
            StringBuilder buffer = new StringBuilder();
            int length = Array.getLength(value);
            for (int index = 0; index < length; index++) {
                Object element = Array.get(value, index);
                buffer.append(property);
                buffer.append("[");
                buffer.append(index);
                buffer.append("]=");
                buffer.append(element);
                buffer.append(";");
            }
            return buffer.toString();
        } else {
            return StringUtility.EMPTY;
        }

    }

    @Override
    public String getEndContent() {
        return StringUtility.EMPTY;
    }

    @Override
    public String toString() {
        return prefix + index + StringUtility.SPACE + operator.getOperate() + StringUtility.SPACE + (operator == AwkOperator.IN ? property : value);
    }

}
