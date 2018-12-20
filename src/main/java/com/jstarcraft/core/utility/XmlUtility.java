package com.jstarcraft.core.utility;

import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

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
	 * 对字符串执行XML1.0转义
	 * 
	 * @param string
	 * @return
	 */
	public static final String escapeXml10(String string) {
		return StringEscapeUtils.escapeXml10(string);
	}

	/**
	 * 对字符串执行XML1.0翻译
	 * 
	 * @param string
	 * @return
	 */
	public static final String unescapeXml10(String string) {
		return StringEscapeUtils.unescapeXml(string);
	}

	/**
	 * 对字符串执行XML1.1转义
	 * 
	 * @param string
	 * @return
	 */
	public static final String escapeXml11(String string) {
		return StringEscapeUtils.escapeXml11(string);
	}

	/**
	 * 对字符串执行XML1.1翻译
	 * 
	 * @param string
	 * @return
	 */
	public static final String unescapeXml11(String string) {
		return StringEscapeUtils.unescapeXml(string);
	}

	/**
	 * 对字符串执行HTML3.0转义
	 * 
	 * @param string
	 * @return
	 */
	public static final String escapeHtml3(String string) {
		return StringEscapeUtils.escapeHtml3(string);
	}

	/**
	 * 对字符串执行HTML3.0翻译
	 * 
	 * @param string
	 * @return
	 */
	public static final String unescapeHtml3(String string) {
		return StringEscapeUtils.unescapeHtml3(string);
	}

	/**
	 * 对字符串执行HTML4.0转义
	 * 
	 * @param string
	 * @return
	 */
	public static final String escapeHtml4(String string) {
		return StringEscapeUtils.escapeHtml4(string);
	}

	/**
	 * 对字符串执行HTML4.0翻译
	 * 
	 * @param string
	 * @return
	 */
	public static final String unescapeHtml4(String string) {
		return StringEscapeUtils.unescapeHtml4(string);
	}

}
