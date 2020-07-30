package com.jstarcraft.core.common.selection.jsonpath;

import java.util.Arrays;
import java.util.Collection;

import org.noear.snack.ONode;

import com.jstarcraft.core.common.selection.JsonPathSelector;

public class SnackJsonPathSelector extends JsonPathSelector<ONode> {

    public SnackJsonPathSelector(String query) {
        super(query);
    }

    @Override
    public Collection<ONode> selectContent(ONode content) {
        content = content.select(query);
        return content.isArray() ? content.ary() : Arrays.asList(content);
    }

}
