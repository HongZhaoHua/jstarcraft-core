package com.jstarcraft.core.common.selection;

import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public Collection<String> selectContent(String content) {
        Collection<String> elements = new LinkedList<>();
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String element = matcher.group(group);
            elements.add(element);
        }
        return elements;
    }

}
