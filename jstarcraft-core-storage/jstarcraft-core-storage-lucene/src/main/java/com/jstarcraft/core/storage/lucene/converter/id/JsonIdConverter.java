package com.jstarcraft.core.storage.lucene.converter.id;

import java.lang.reflect.Type;

import com.jstarcraft.core.common.conversion.json.JsonUtility;
import com.jstarcraft.core.storage.lucene.converter.IdConverter;

/**
 * JSON标识转换器
 * 
 * @author Birdy
 *
 */
public class JsonIdConverter implements IdConverter {

    @Override
    public String convert(Type type, Object id) {
        return JsonUtility.object2String(id);
    }

}
