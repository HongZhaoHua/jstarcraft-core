package com.jstarcraft.core.common.selection.jsonpath;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configuration.ConfigurationBuilder;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.path.EvaluationContextImpl;
import com.jayway.jsonpath.internal.path.PathCompiler;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jstarcraft.core.common.reflection.ReflectionUtility;

/**
 * JSONPath选择器
 * 
 * <pre>
 * 基于Jayway
 * </pre>
 * 
 * @author Birdy
 *
 * @param <T>
 */
public class JaywayJsonPathSelector<T> extends JsonPathSelector<T> {

    /** 通过反射获取Jayway属性 */
    private static final Field valueResult;

    static {
        try {
            valueResult = EvaluationContextImpl.class.getDeclaredField("valueResult");
            ReflectionUtility.makeAccessible(valueResult);
        } catch (Exception exception) {
            throw new Error(exception);
        }
    }

    private Configuration configuration;

    private Path path;

    public JaywayJsonPathSelector(String query) {
        this(query, null);
    }

    public JaywayJsonPathSelector(String query, JsonProvider adapter) {
        super(query);

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.jsonProvider(adapter);
        this.configuration = builder.build();
        this.path = PathCompiler.compile(query);
    }

    @Override
    public List<T> selectMultiple(T content) {
        EvaluationContext context = path.evaluate(content, content, configuration);
        Object nodes = ReflectionUtility.getField(valueResult, context);
        JsonProvider adapter = context.configuration().jsonProvider();
        int size = adapter.length(nodes);
        ArrayList<T> elements = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            T node = (T) adapter.getArrayIndex(nodes, index);
            elements.add(node);
        }
        return elements;
    }

}
