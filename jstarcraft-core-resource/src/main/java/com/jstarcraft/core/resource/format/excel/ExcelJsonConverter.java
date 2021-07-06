package com.jstarcraft.core.resource.format.excel;

import java.lang.reflect.Field;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.jstarcraft.core.common.conversion.json.JsonUtility;

/**
 * Excel转换器
 * 
 * @author Birdy
 *
 */
public class ExcelJsonConverter implements Converter {

    @Override
    public Class supportJavaTypeKey() {
        return null;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    @Override
    public Object convertToJavaData(CellData data, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String json = data.getStringValue();
        Field field = contentProperty.getField();
        Object value = JsonUtility.string2Object(json, field.getGenericType());
        return value;
    }

    @Override
    public CellData convertToExcelData(Object value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        Object json = JsonUtility.object2String(value);
        CellData data = new CellData<>(json);
        return data;
    }

}
