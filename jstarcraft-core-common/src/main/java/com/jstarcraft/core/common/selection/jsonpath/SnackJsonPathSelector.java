package com.jstarcraft.core.common.selection.jsonpath;

import java.util.Arrays;
import java.util.List;

import org.noear.snack.ONode;

/**
 * JSONPath选择器
 * 
 * <pre>
 * 基于Snack3
 * </pre>
 * 
 * @author Birdy
 *
 */
public class SnackJsonPathSelector extends JsonPathSelector<ONode> {

    public SnackJsonPathSelector(String query) {
        super(query);
    }

    @Override
    public List<ONode> selectMultiple(ONode content) {
        content = content.select(query);
        return content.isArray() ? content.ary() : Arrays.asList(content);
    }

}
