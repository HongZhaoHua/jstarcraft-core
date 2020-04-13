package com.jstarcraft.core.resource.format;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.jstarcraft.core.common.conversion.ConversionUtility;
import com.jstarcraft.core.common.reflection.ReflectionUtility;
import com.jstarcraft.core.resource.exception.StorageException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * XLSX适配器
 * 
 * @author Birdy
 *
 */
public class XlsxFormatAdapter implements FormatAdapter {

    private final static Logger logger = LoggerFactory.getLogger(XlsxFormatAdapter.class);

    /** 属性标识 */
    private final static String ATTRIBUTE = "ATTRIBUTE";
    /** 完成标识 */
    private final static String COMPLETE = "COMPLETE";
    /** 忽略标识 */
    private final static String IGNORE = "IGNORE";

    /** 开始标记 */
    private final String startMark;
    /** 结束标记 */
    private final String stopMark;
    /** 忽略标记 */
    private final Collection<String> ignoreMarks;

    public XlsxFormatAdapter() {
        this(ATTRIBUTE, COMPLETE, new HashSet<>());
    }

    public XlsxFormatAdapter(String startMark, String stopMark, Collection<String> ignoreMarks) {
        this.startMark = startMark;
        this.stopMark = stopMark;
        this.ignoreMarks = new HashSet<>(ignoreMarks);
        this.ignoreMarks.add(IGNORE);
    }

    private <E> List<E> process(Class<E> clazz, Constructor<E> constructor, SharedStringsTable references, InputStream sheetInputStream) throws Exception {
        InputSource sheetSource = new InputSource(sheetInputStream);
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxFactory.newSAXParser();
        XMLReader sheetParser = saxParser.getXMLReader();
        XlsxProcessor<E> handler = new XlsxProcessor<E>(clazz, constructor, references);
        sheetParser.setContentHandler(handler);
        sheetParser.parse(sheetSource);
        return handler.instances;
    }

    @Override
    public <E> Iterator<E> iterator(Class<E> clazz, InputStream stream) {
        // 实例列表
        List<E> instances = new LinkedList<>();
        try {
            OPCPackage xlsxPackage = OPCPackage.open(stream);
            XSSFReader xssfParser = new XSSFReader(xlsxPackage);
            SharedStringsTable references = xssfParser.getSharedStringsTable();
            Constructor<E> constructor = clazz.getDeclaredConstructor();
            ReflectionUtility.makeAccessible(constructor);
            // 按照页遍历
            XSSFReader.SheetIterator iterator = (XSSFReader.SheetIterator) xssfParser.getSheetsData();
            while (iterator.hasNext()) {
                // 按照每页的属性定义
                InputStream sheetStream = iterator.next();
                List<E> values = process(clazz, constructor, references, sheetStream);
                instances.addAll(values);
                sheetStream.close();
            }
            return instances.iterator();
        } catch (Exception exception) {
            throw new StorageException("遍历XLSX异常", exception);
        }
    }

    private class Attribute {

        /** 仓储字段 */
        public final Field field;
        /** 第几列 */
        public final int index;

        Attribute(Class<?> clazz, int index, String name) throws Exception {
            this.index = index;
            this.field = ReflectionUtility.findField(clazz, name);
            ReflectionUtility.makeAccessible(field);
        }

        private void set(Object instance, String content) throws Exception {
            Object value = ConversionUtility.convert(content, field.getGenericType());
            field.set(instance, value);
        }

    }

    private enum DataType {

        BOOLEAN, ERROR, FORMULA, NUMBER, STRING, REFERENCE;

    }

    private class XlsxProcessor<E> extends DefaultHandler {

        /** 解析过程属性 */
        private DataType dataType = DataType.NUMBER;
        private int fromColumn = -1;
        private int toColumn = -1;
        private int row = 0;
        private boolean isFormula;
        private boolean isValue;
        private StringBuffer formula = new StringBuffer();
        private StringBuffer value = new StringBuffer();
        private boolean start;
        private boolean stop;

        private final List<Attribute> attributes = new LinkedList<>();
        private final Class<E> clazz;
        private final Constructor<E> constructor;
        private final List<String> columns = new LinkedList<>();
        private final List<E> instances = new LinkedList<>();

        /** 共享字符串引用表 */
        private final SharedStringsTable references;

        public XlsxProcessor(Class<E> clazz, Constructor<E> constructor, SharedStringsTable references) {
            this.clazz = clazz;
            this.constructor = constructor;
            this.references = references;
        }

        /**
         * 按照字母表将字符串转换为数字
         * 
         * @param name
         * @return
         */
        private int parseColumn(String name) {
            int length = name.length();
            length = length - 1;
            int column = (length) * 26 + name.charAt(length) - 'A';
            return column;
        }

        private void parseRow() throws Exception {
            if (stop) {
                return;
            }
            // 第一列永远为标记
            String mark = columns.get(0);
            if (StringUtility.isNotBlank(mark)) {
                if (startMark.equalsIgnoreCase(mark)) {
                    // 表头属性信息集合
                    for (int index = 1; index < columns.size(); index++) {
                        String name = columns.get(index);
                        if (StringUtility.isBlank(name)) {
                            continue;
                        }
                        try {
                            Attribute attribure = new Attribute(clazz, index, name);
                            attributes.add(attribure);
                        } catch (Exception exception) {
                            throw new StorageException(exception);
                        }
                    }
                    start = true;
                    return;
                }
                if (stopMark.equalsIgnoreCase(mark)) {
                    stop = true;
                }
                if (ignoreMarks.contains(mark)) {
                    // 忽略此行数据
                    return;
                }
            }
            if (!start) {
                return;
            }
            E instance = constructor.newInstance();
            for (Attribute attribute : attributes) {
                int index = attribute.index;
                if (index > columns.size() - 1) {
                    continue;
                }
                String content = columns.get(index);
                if (StringUtility.isBlank(content)) {
                    continue;
                }
                attribute.set(instance, content);
            }
            instances.add(instance);

        }

        @Override
        public void characters(char[] characters, int index, int length) throws SAXException {
            if (isValue) {
                value.append(characters, index, length);
            }
            if (isFormula) {
                formula.append(characters, index, length);
            }
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            if ("c".equals(name)) {
                // 根据Cell的引用得到所在列(格式为\w+\d+)
                // 列部分由字母组成,行部分由数字组成
                String reference = attributes.getValue("r");
                int digit = -1;
                for (int index = 0; index < reference.length(); ++index) {
                    if (Character.isDigit(reference.charAt(index))) {
                        digit = index;
                        break;
                    }
                }
                toColumn = parseColumn(reference.substring(0, digit));
                dataType = DataType.NUMBER;
                String cellType = attributes.getValue("t");
                if ("b".equals(cellType))
                    dataType = DataType.BOOLEAN;
                else if ("e".equals(cellType))
                    dataType = DataType.ERROR;
                else if ("inlineStr".equals(cellType))
                    dataType = DataType.STRING;
                else if ("s".equals(cellType))
                    dataType = DataType.REFERENCE;
                else if ("str".equals(cellType))
                    dataType = DataType.FORMULA;
            }
            if ("f".equals(name)) {
                if (dataType == DataType.NUMBER) {
                    dataType = DataType.FORMULA;
                }
                String cellType = attributes.getValue("t");
                if (cellType != null && cellType.equals("shared")) {
                    // TODO 不支持的公式类型
                } else {
                    isFormula = true;
                }
                formula.setLength(0);
            }
            if ("inlineStr".equals(name) || "v".equals(name)) {
                isValue = true;
                value.setLength(0);
            }
        }

        @Override
        public void endElement(String uri, String localName, String name) throws SAXException {
            if ("f".equals(name)) {
                isFormula = false;
            }
            String string = null;
            if ("v".equals(name)) {
                switch (dataType) {
                case BOOLEAN:
                    string = value.charAt(0) == '0' ? "FALSE" : "TRUE";
                    break;
                case ERROR:
                    string = "\"ERROR:" + value.toString() + '"';
                    break;
                case FORMULA:
                    string = formula.toString();
                    break;
                case NUMBER:
                    string = value.toString();
                    break;
                case STRING:
                    XSSFRichTextString text = new XSSFRichTextString(value.toString());
                    string = text.toString();
                    break;
                case REFERENCE:
                    try {
                        int index = Integer.parseInt(value.toString());
                        XSSFRichTextString reference = new XSSFRichTextString(references.getEntryAt(index));
                        string = reference.toString();
                    } catch (NumberFormatException exception) {
                        throw new StorageException(exception);
                    }
                    break;
                default:
                    string = "(TODO: Unknow type: " + dataType + ")";
                    break;
                }
                for (int index = fromColumn + 1; index < toColumn; ++index) {
                    columns.add(null);
                }
                if (fromColumn == -1) {
                    fromColumn = 0;
                }
                if (toColumn > -1) {
                    fromColumn = toColumn;
                    columns.add(string);
                }
            }
            if ("row".equals(name)) {
                try {
                    parseRow();
                } catch (Exception exception) {
                    throw new StorageException(exception);
                } finally {
                    fromColumn = -1;
                    columns.clear();
                    row++;
                }
            }
        }

    }

}
