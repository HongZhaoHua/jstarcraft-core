package com.jstarcraft.core.common.selection.jsonpath;

import java.util.ArrayList;
import java.util.List;

import org.jsfr.json.JsonSurfer;
import org.jsfr.json.compiler.JsonPathCompiler;
import org.jsfr.json.path.JsonPath;

/**
 * JSONPath选择器
 * 
 * <pre>
 * 基于JsonSurfer
 * </pre>
 * 
 * @author Birdy
 *
 * @param <T>
 */
public class JsonSurferJsonPathSelector extends JsonPathSelector<String> {

    private JsonSurfer adapter;

    private JsonPath path;

    public JsonSurferJsonPathSelector(String query, JsonSurfer adapter) {
        super(query);

        this.adapter = adapter;
        this.path = JsonPathCompiler.compile(query);
    }

    @Override
    public List<String> selectMultiple(String content) {
        List<String> elements = new ArrayList<>();
        adapter.configBuilder().bind(path, (value, context) -> {
            if (value instanceof CharSequence) {
                elements.add("\"" + value.toString() + "\"");
            } else {
                elements.add(value.toString());
            }
        }).buildAndSurf(content);
        return elements;
    }

}
