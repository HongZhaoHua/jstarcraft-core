package com.jstarcraft.core.common.conversion.csv;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * CSV信息
 * 
 * @author Birdy
 *
 */
class CsvInformation {

    private final Constructor constructor;

    private final Field[] fields;

    CsvInformation(Constructor constructor, Field[] fields) {
        this.constructor = constructor;
        this.fields = fields;
    }

    public Constructor getConstructor() {
        return constructor;
    }

    public Field[] getFields() {
        return fields;
    }

}
