package com.jstarcraft.core.common.option;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.jstarcraft.core.common.conversion.yaml.YamlUtility;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * YAML配置器
 * 
 * @author Birdy
 *
 */
public class YamlOption implements StringOption {

    /** 配置项 */
    private Map<String, String> keyValues;

    protected void flatten(String path, Map<String, Object> from, Map<String, String> to) {
        Set<Entry<String, Object>> keyValues = from.entrySet();
        for (Iterator<Entry<String, Object>> iterator = keyValues.iterator(); iterator.hasNext();) {
            Entry<String, Object> keyValue = iterator.next();
            String key = keyValue.getKey();
            Object value = keyValue.getValue();
            key = StringUtility.isEmpty(path) ? key : key.startsWith("[") ? path.concat(key) : path.concat(StringUtility.DOT).concat(key);
            if (value instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) value;
                flatten(key, map, to);
                continue;
            } else if (value instanceof Collection) {
                int count = 0;
                Collection<Object> collection = (Collection<Object>) value;
                for (Object element : collection) {
                    flatten(key, Collections.singletonMap("[" + (count++) + "]", element), to);
                }
                continue;
            }
            to.put(key, value.toString());
        }
    }

    public YamlOption(String yaml) {
        this.keyValues = new LinkedHashMap<>();
        Type type = TypeUtility.parameterize(LinkedHashMap.class, String.class, Object.class);
        flatten(StringUtility.EMPTY, YamlUtility.string2Object(yaml, type), keyValues);
    }

    @Override
    public String getString(String key) {
        return keyValues.get(key);
    }

    @Override
    public Iterator<String> getKeys() {
        return keyValues.keySet().iterator();
    }

}
