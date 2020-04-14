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

/**
 * Excel适配器
 * 
 * @author Birdy
 *
 */
public class ExcelFormatAdapter implements FormatAdapter {

    private final static Logger logger = LoggerFactory.getLogger(ExcelFormatAdapter.class);

    private final class ExcelFormatListener<E> extends AnalysisEventListener<E> {

        /** 实例列表 */
        private List<E> instances = new LinkedList<>();

        @Override
        public void onException(Exception exception, AnalysisContext context) {
            if (exception instanceof ExcelDataConvertException) {
                ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) exception;
                logger.error("遍历Excel第{}行,第{}列异常,数据为:{}", excelDataConvertException.getRowIndex(), excelDataConvertException.getColumnIndex(), excelDataConvertException.getCellData());
            }
        }

        @Override
        public void invokeHeadMap(Map<Integer, String> meta, AnalysisContext context) {
            logger.info("遍历Excel元数据:{}", meta);
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
            ExcelFormatListener<E> listener = new ExcelFormatListener<>();
            EasyExcel.read(stream, clazz, listener).headRowNumber(metaNumber).doReadAll();
            return listener.getInstances().iterator();
        } catch (Exception exception) {
            throw new StorageException("遍历Excel异常", exception);
        }
    }

}
