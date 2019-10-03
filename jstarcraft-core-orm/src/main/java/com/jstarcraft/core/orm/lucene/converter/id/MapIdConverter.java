package com.jstarcraft.core.orm.lucene.converter.id;

import java.lang.reflect.Type;

import com.jstarcraft.core.common.conversion.json.JsonUtility;
import com.jstarcraft.core.orm.lucene.converter.IdConverter;

/**
 * 映射标识转换器
 * 
 * @author Birdy
 *
 */
//TODO 暂时使用JSON格式
public class MapIdConverter implements IdConverter {

    @Override
    public Object decode(Type type, String data) {
        return JsonUtility.string2Object(data, type);
    }

    @Override
    public String encode(Type type, Object id) {
        return JsonUtility.object2String(id);
    }

}
