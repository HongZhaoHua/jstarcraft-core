package com.jstarcraft.core.common.selection.regular;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jstarcraft.core.common.selection.AbstractSelector;

/**
 * 正则选择器
 * 
 * @author Birdy
 *
 */
public class RegularSelector extends AbstractSelector<String> {

    private Pattern pattern;

    private int group;

    public RegularSelector(String query) {
        this(query, 0);
    }

    public RegularSelector(String query, int flag) {
        this(query, 0, 0);
    }

    public RegularSelector(String query, int flag, int group) {
        super(query);
        this.pattern = Pattern.compile(query, flag);
        this.group = group;
    }

    @Override
    public String selectSingle(String content) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String element = matcher.group(group);
            return element;
        }
        return null;
    }

    @Override
    public List<String> selectMultiple(String content) {
        List<String> elements = new LinkedList<>();
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String element = matcher.group(group);
            elements.add(element);
        }
        return elements;
    }

}
