package com.jstarcraft.core.resource.format;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.jstarcraft.core.common.conversion.csv.CsvUtility;
import com.jstarcraft.core.resource.exception.StorageException;

/**
 * CSV适配器
 * 
 * @author Birdy
 */
public class CsvFormatAdapter implements FormatAdapter {

    /** 分隔符 */
    protected char delimiter;

    public CsvFormatAdapter(char delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public <E> Iterator<E> iterator(Class<E> clazz, InputStream stream) {
        try {
            List<E> list = new LinkedList<>();
            try (InputStreamReader reader = new InputStreamReader(stream); BufferedReader buffer = new BufferedReader(reader)) {
                for (String line = buffer.readLine(); line != null; line = buffer.readLine()) {
                    E instance = CsvUtility.string2Object(line, clazz);
                    list.add(instance);
                }
            }
            return list.iterator();
        } catch (Exception exception) {
            throw new StorageException("遍历CSV异常", exception);
        }
    }

}
