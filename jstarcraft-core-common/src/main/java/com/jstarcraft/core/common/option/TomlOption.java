package com.jstarcraft.core.common.option;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlTable;

import com.jstarcraft.core.utility.StringUtility;

public class TomlOption implements StringOption {

    /** 配置项 */
    private Map<String, String> keyValues;

    private void flatten(String path, TomlTable table, Map<String, String> keyValues) {
        Set<List<String>> keys = table.keyPathSet();
        for (List<String> key : keys) {
            if (table.isTable(key)) {
                flatten(String.join(StringUtility.DOT, key), table.getTable(key), keyValues);
            }
            if (table.isArray(key)) {
                flatten(String.join(StringUtility.DOT, key), table.getArray(key), keyValues);
            }
            keyValues.put(String.join(StringUtility.DOT, key), table.get(path).toString());
        }
    }

    private void flatten(String path, TomlArray array, Map<String, String> keyValues) {
        for (int index = 0, size = array.size(); index < size; index++) {
            String key = String.join(StringUtility.EMPTY, path, "[" + index + "]");
            if (array.containsTables()) {
                flatten(key, array.getTable(index), keyValues);
                continue;
            }
            if (array.containsArrays()) {
                flatten(key, array.getTable(index), keyValues);
            }
            keyValues.put(key, array.get(index).toString());
        }
    }

    public TomlOption(String toml) {
        this.keyValues = new LinkedHashMap<>();
        TomlTable table = Toml.parse(toml);
        flatten(StringUtility.EMPTY, table, keyValues);
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
