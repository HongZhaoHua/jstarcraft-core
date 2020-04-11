package com.jstarcraft.core.common.conversion.xml;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.jstarcraft.core.utility.StringUtility;

/**
 * XML工具
 * 
 * @author Birdy
 */
public class XmlUtility extends DomUtils {

    private static final Logger logger = LoggerFactory.getLogger(XmlUtility.class);

    /**
     * 获取元素中指定标签的唯一元素
     * 
     * @param element
     * @param tag
     * @return
     */
    public static Element getUniqueElement(Element element, String tag) {
        List<Element> elements = DomUtils.getChildElementsByTagName(element, tag);
        if (elements.size() != 1) {
            String message = StringUtility.format("指定的标签[{}]在元素[{}]中不是唯一", tag, element);
            logger.error(message);
            throw new IllegalArgumentException(message);
        }
        return elements.get(0);
    }

    /**
     * 对字符串执行XML1.0加密
     * 
     * @param string
     * @return
     */
    public static final String escapeXml10(String string) {
        return StringEscapeUtils.escapeXml10(string);
    }

    /**
     * 对字符串执行XML1.0解密
     * 
     * @param string
     * @return
     */
    public static final String unescapeXml10(String string) {
        return StringEscapeUtils.unescapeXml(string);
    }

    /**
     * 对字符串执行XML1.1加密
     * 
     * @param string
     * @return
     */
    public static final String escapeXml11(String string) {
        return StringEscapeUtils.escapeXml11(string);
    }

    /**
     * 对字符串执行XML1.1解密
     * 
     * @param string
     * @return
     */
    public static final String unescapeXml11(String string) {
        return StringEscapeUtils.unescapeXml(string);
    }

    /**
     * 对字符串执行HTML3.0加密
     * 
     * @param string
     * @return
     */
    public static final String escapeHtml3(String string) {
        return StringEscapeUtils.escapeHtml3(string);
    }

    /**
     * 对字符串执行HTML3.0解密
     * 
     * @param string
     * @return
     */
    public static final String unescapeHtml3(String string) {
        return StringEscapeUtils.unescapeHtml3(string);
    }

    /**
     * 对字符串执行HTML4.0加密
     * 
     * @param string
     * @return
     */
    public static final String escapeHtml4(String string) {
        return StringEscapeUtils.escapeHtml4(string);
    }

    /**
     * 对字符串执行HTML4.0解密
     * 
     * @param string
     * @return
     */
    public static final String unescapeHtml4(String string) {
        return StringEscapeUtils.unescapeHtml4(string);
    }

    public static class Wrapper<T> {

        private List<T> instances;

        public Wrapper() {
            instances = new ArrayList<T>();
        }

        public Wrapper(List<T> instances) {
            this.instances = instances;
        }

        @XmlAnyElement(lax = true)
        public List<T> getInstances() {
            return instances;
        }

    }

    public static <T> List<T> unmarshal(Class<T> clazz, String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Wrapper.class, clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(xml);
        Wrapper<T> wrapper = (Wrapper<T>) unmarshaller.unmarshal(new StreamSource(reader), Wrapper.class).getValue();
        return wrapper.getInstances();
    }

    public static <T> String marshal(Class<T> clazz, List<T> list, String name) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Wrapper.class, clazz);
        Marshaller marshaller = context.createMarshaller();
        StringWriter writer = new StringWriter();
        JAXBElement<Wrapper> jaxbElement = new JAXBElement<>(new QName(name), Wrapper.class, new Wrapper<T>(list));
        marshaller.marshal(jaxbElement, writer);
        return writer.getBuffer().toString();
    }

}
