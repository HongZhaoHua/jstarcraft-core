package com.jstarcraft.core.codec.csv;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.jstarcraft.core.codec.csv.converter.CsvContext;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.utility.StringUtility;

/**
 * CSV读入器
 * 
 * @author Birdy
 *
 */
public class CsvReader extends CsvContext {

    private Iterator<String> inputStream;

    public CsvReader(InputStream inputStream, CodecDefinition definition) {
        super(definition);
        InputStreamReader buffer = new InputStreamReader(inputStream, StringUtility.CHARSET);
        try (CSVParser input = new CSVParser(buffer, FORMAT)) {
            Iterator<CSVRecord> iterator = input.iterator();
            if (iterator.hasNext()) {
                CSVRecord values = iterator.next();
                this.inputStream = values.iterator();
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public Iterator<String> getInputStream() {
        return inputStream;
    }

}
