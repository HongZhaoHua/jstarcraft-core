package com.jstarcraft.core.common.conversion.csv.query;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;

import com.jstarcraft.core.utility.StringUtility;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/***
 * AWK命令
 * 
 * <pre>
 * 注意以下两点:
 * FPAT参数在AWK 4以上版本才支持.
 * FPAT参数中的正则表达式需要转义.
 * </pre>
 * 
 * @author Birdy
 */
public class AwkCommand {

    /** AWK的特殊字符 */
    private static final Map<String, String> awkSpecialCharacters = new LinkedHashMap<>();

    static {
        awkSpecialCharacters.put("\"", "\\x22");
        awkSpecialCharacters.put("'", "\\x27");
        awkSpecialCharacters.put("^", "\\x5E");
        awkSpecialCharacters.put("|", "\\x7C");
    }

    /** 正则的特殊字符 */
    private static final String[] regularSpecialCharacters = { "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };

    /** AWK命令 */
    private static final String AWK = "gawk ";

    /** 开始模板 */
    private static final String BEGIN = "\"BEGIN{FPAT=\\\"{}\\\";{}{}}";

    /** 正则模板(([^{}{}]*)|({}([^{}]|{}{2}|[u4E00-u9FA5]+{})+[u4E00-u9FA5;]{})) */
    private static final String REGULAR = "([^{}{}]*)|({}([^{}]|{}{2}|[u4E00-u9FA5]+{})+[u4E00-u9FA5" + StringUtility.SEMICOLON + "]{})";

    /** 指令模板 */
    private static final String COMMAND = "{}{{}}";

    /** 结束模板 */
    private static final String END = "END{{}}\"";

    /** 条件映射 */
    private final Int2ObjectMap<AwkCondition> conditions = new Int2ObjectOpenHashMap<>();

    /** 属性列表 */
    private final String[] properties;

    /** 正则 */
    private final String regular;

    public AwkCommand(String delimiter, String quoter, String... properties) {
        for (String regularSpecialCharacter : regularSpecialCharacters) {
            delimiter = delimiter.replace(regularSpecialCharacter, "\\" + regularSpecialCharacter);
            quoter = quoter.replace(regularSpecialCharacter, "\\" + regularSpecialCharacter);
        }
        this.properties = Arrays.copyOf(properties, properties.length);
        String regular = StringUtility.format(REGULAR, delimiter, quoter, quoter, quoter, quoter, quoter, quoter);
        for (Entry<String, String> keyValue : awkSpecialCharacters.entrySet()) {
            String key = keyValue.getKey();
            String value = keyValue.getValue();
            regular = regular.replace(key, value);
        }
        this.regular = regular;
    }

    private final String format(AwkCondition condition, String begin, String command, String end, String... paths) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(AWK);
        begin = StringUtility.format(BEGIN, regular, condition.getBeginContent(), begin);
        buffer.append(begin);
        command = StringUtility.format(COMMAND, condition, command);
        buffer.append(command);
        end = StringUtility.format(END, end);
        buffer.append(end);
        for (String path : paths) {
            buffer.append(StringUtility.SPACE);
            buffer.append(path);
        }
        return buffer.toString();
    }

    public String count(AwkCondition condition, String... paths) {
        return format(condition, "count=0;", "count=count+1;", "print count;", paths);
    };

    public String query(AwkCondition condition, String... paths) {
        return format(condition, StringUtility.EMPTY, "print;", StringUtility.EMPTY, paths);
    }

    public AwkCondition equal(String property, Object value) {
        int index = ArrayUtils.indexOf(properties, property);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        SimpleCondition condition = new SimpleCondition(AwkOperator.EQUAL, index + 1, property + conditions.size(), value);
        conditions.put(conditions.size(), condition);
        return condition;
    }

    public AwkCondition greaterEqual(String property, Object value) {
        int index = ArrayUtils.indexOf(properties, property);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        SimpleCondition condition = new SimpleCondition(AwkOperator.GREATER_EQUAL, index + 1, property + conditions.size(), value);
        conditions.put(conditions.size(), condition);
        return condition;
    }

    public AwkCondition greaterThan(String property, Object value) {
        int index = ArrayUtils.indexOf(properties, property);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        SimpleCondition condition = new SimpleCondition(AwkOperator.GREATER_THAN, index + 1, property + conditions.size(), value);
        conditions.put(conditions.size(), condition);
        return condition;
    }

    public AwkCondition in(String property, Object... values) {
        int index = ArrayUtils.indexOf(properties, property);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        SimpleCondition condition = new SimpleCondition(AwkOperator.IN, index + 1, property + conditions.size(), values);
        conditions.put(conditions.size(), condition);
        return condition;
    }

    public AwkCondition lessEqual(String property, Object value) {
        int index = ArrayUtils.indexOf(properties, property);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        SimpleCondition condition = new SimpleCondition(AwkOperator.LESS_EQUAL, index + 1, property + conditions.size(), value);
        conditions.put(conditions.size(), condition);
        return condition;
    }

    public AwkCondition lessThan(String property, Object value) {
        int index = ArrayUtils.indexOf(properties, property);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        SimpleCondition condition = new SimpleCondition(AwkOperator.LESS_THAN, index + 1, property + conditions.size(), value);
        conditions.put(conditions.size(), condition);
        return condition;
    }

    public AwkCondition not(String property, Object value) {
        int index = ArrayUtils.indexOf(properties, property);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        SimpleCondition condition = new SimpleCondition(AwkOperator.NOT, index + 1, property + conditions.size(), value);
        conditions.put(conditions.size(), condition);
        return condition;
    }

    public AwkCondition and(AwkCondition left, AwkCondition right) {
        ComplexCondition condition = new ComplexCondition(AwkOperator.AND, left, right);
        conditions.put(conditions.size(), condition);
        return condition;
    }

    public AwkCondition or(AwkCondition left, AwkCondition right) {
        ComplexCondition condition = new ComplexCondition(AwkOperator.OR, left, right);
        conditions.put(conditions.size(), condition);
        return condition;
    }

}