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
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.CellExtra;
import com.jstarcraft.core.resource.exception.StorageException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * Excel适配器
 * 
 * @author Birdy
 *
 */
public class XlsFormatAdapter implements FormatAdapter {

    private final static Logger logger = LoggerFactory.getLogger(XlsFormatAdapter.class);

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

        /**
         * 读取额外信息
         * https://easyexcel.opensource.alibaba.com/docs/current/quickstart/read#%E9%A2%9D%E5%A4%96%E4%BF%A1%E6%81%AF%E6%89%B9%E6%B3%A8%E8%B6%85%E9%93%BE%E6%8E%A5%E5%90%88%E5%B9%B6%E5%8D%95%E5%85%83%E6%A0%BC%E4%BF%A1%E6%81%AF%E8%AF%BB%E5%8F%96
         */
        @Override
        public void extra(CellExtra extra, AnalysisContext context) {
            CellExtraTypeEnum type = extra.getType();
            String text = extra.getText();
            Integer rowIndex = extra.getRowIndex();
            Integer columnIndex = extra.getColumnIndex();
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
        }

        List<E> getInstances() {
            return instances;
        }

    }

    private int metaNumber;

    public XlsFormatAdapter() {
        this(1);
    }

    public XlsFormatAdapter(int metaNumber) {
        this.metaNumber = metaNumber;
    }

    @Override
    public <E> Iterator<E> iterator(Class<E> clazz, InputStream stream) {
        try {
            ExcelFormatListener<E> listener = new ExcelFormatListener<>(clazz);
            EasyExcel.read(stream, clazz, listener).headRowNumber(metaNumber)
                    // 读取批注,默认不读取
                    .extraRead(CellExtraTypeEnum.COMMENT)
                    // 读取超链接,默认不读取
                    .extraRead(CellExtraTypeEnum.HYPERLINK)
                    // 读取合并单元,默认不读取
                    .extraRead(CellExtraTypeEnum.MERGE).doReadAll();
            return listener.getInstances().iterator();
        } catch (Exception exception) {
            throw new StorageException("遍历Excel异常", exception);
        }
    }

}
