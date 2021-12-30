package com.jstarcraft.core.resource.format;

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.jstarcraft.core.resource.exception.StorageException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Excel适配器
 * 
 * @author Birdy
 *
 */
public class ExcelFormatAdapter implements FormatAdapter {

    private final static Logger logger = LoggerFactory.getLogger(ExcelFormatAdapter.class);

    private final class ExcelFormatListener<E> extends AnalysisEventListener<E> {

        private Class<E> clazz;

        /** 实例列表 */
        private List<E> instances = new LinkedList<>();

        private ExcelFormatListener(Class<E> clazz) {
            this.clazz = clazz;
        }

        @Override
        public void onException(Exception exception, AnalysisContext context) {
            if (exception instanceof ExcelDataConvertException) {
                ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) exception;
                String message = StringUtility.format("遍历Excel[{}]第{}行,第{}列异常,数据为:{}", clazz.getName(), excelDataConvertException.getRowIndex(), excelDataConvertException.getColumnIndex(), excelDataConvertException.getCellData());
                logger.error(message);
                throw new StorageException(message, exception);
            }
        }

        @Override
        public void invokeHeadMap(Map<Integer, String> meta, AnalysisContext context) {
            String message = StringUtility.format("遍历Excel[{}]元数据:{}", clazz.getName(), meta);
            logger.info(message);
        }

        @Override
        public void invoke(E data, AnalysisContext context) {
            instances.add(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
        }

        List<E> getInstances() {
            return instances;
        }

    }

    private int metaNumber;

    public ExcelFormatAdapter() {
        this(1);
    }

    public ExcelFormatAdapter(int metaNumber) {
        this.metaNumber = metaNumber;
    }

    @Override
    public <E> Iterator<E> iterator(Class<E> clazz, InputStream stream) {
        try {
            ExcelFormatListener<E> listener = new ExcelFormatListener<>(clazz);
            EasyExcel.read(stream, clazz, listener).headRowNumber(metaNumber).doReadAll();
            return listener.getInstances().iterator();
        } catch (Exception exception) {
            throw new StorageException("遍历Excel异常", exception);
        }
    }

}
