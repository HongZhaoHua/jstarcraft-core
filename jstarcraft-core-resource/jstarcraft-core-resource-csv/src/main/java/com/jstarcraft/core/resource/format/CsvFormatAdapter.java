package com.jstarcraft.core.resource.format;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.read.metadata.holder.ReadWorkbookHolder;
import com.alibaba.excel.read.metadata.holder.csv.CsvReadWorkbookHolder;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.jstarcraft.core.common.conversion.csv.CsvUtility;
import com.jstarcraft.core.resource.exception.StorageException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * CSV适配器
 * 
 * @author Birdy
 */
public class CsvFormatAdapter implements FormatAdapter {

    private final static Logger logger = LoggerFactory.getLogger(CsvFormatAdapter.class);

    public final static class CsvFormatListener<E> extends AnalysisEventListener<E> {

        private Class<E> clazz;

        /** 实例列表 */
        private List<E> instances = new LinkedList<>();

        public CsvFormatListener(Class<E> clazz) {
            this.clazz = clazz;
        }

        @Override
        public void onException(Exception exception, AnalysisContext context) {
            if (exception instanceof ExcelDataConvertException) {
                ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) exception;
                String message = StringUtility.format("遍历CSV[{}]第{}行,第{}列异常,数据为:{}", clazz.getName(), excelDataConvertException.getRowIndex(), excelDataConvertException.getColumnIndex(), excelDataConvertException.getCellData());
                logger.error(message);
                throw new StorageException(message, exception);
            }
        }

        @Override
        public void invokeHeadMap(Map<Integer, String> meta, AnalysisContext context) {
            String message = StringUtility.format("遍历CSV[{}]元数据:{}", clazz.getName(), meta);
            logger.info(message);
        }

        @Override
        public void invoke(E data, AnalysisContext context) {
            instances.add(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
        }

        public List<E> getInstances() {
            return instances;
        }

    }

    /** 分隔符 */
    protected char delimiter;

    private int metaNumber;

    public CsvFormatAdapter() {
        this(',', 1);
    }

    public CsvFormatAdapter(char delimiter, int metaNumber) {
        this.delimiter = delimiter;
        this.metaNumber = metaNumber;
    }

    @Override
    public <E> Iterator<E> iterator(Class<E> clazz, InputStream stream) {
        try {
            CsvFormatListener<E> listener = new CsvFormatListener<>(clazz);
            ExcelReader reader = EasyExcel.read(stream, clazz, listener).excelType(ExcelTypeEnum.CSV).headRowNumber(metaNumber).build();
            CsvReadWorkbookHolder holder = (CsvReadWorkbookHolder) reader.analysisContext().readWorkbookHolder();
            holder.setCsvFormat(holder.getCsvFormat().withDelimiter(delimiter));
            reader.readAll();
            return listener.getInstances().iterator();
        } catch (Exception exception) {
            throw new StorageException("遍历CSV异常", exception);
        }
    }

}
