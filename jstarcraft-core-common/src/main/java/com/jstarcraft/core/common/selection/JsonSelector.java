package com.jstarcraft.core.common.selection;

import java.util.Collection;

import com.jayway.jsonpath.JsonPath;

/**
 * JSON选择器
 * 
 * @author Birdy
 *
 */
public class JsonSelector extends AbstractSelector<String> {

    public JsonSelector(String query) {
        super(query);
    }

    @Override
    public Collection<String> selectContent(String content) {
        JsonPath.read(content, query);
        return null;
    }

}
