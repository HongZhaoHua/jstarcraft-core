package com.jstarcraft.core.common.configuration;

import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.jstarcraft.core.utility.StringUtility;

/**
 * XML配置器
 * 
 * @author Birdy
 *
 */
public class XmlConfigurator implements StringProfile {

    /** 配置项 */
    private Map<String, String> keyValues;

    private void flatten(String path, NamedNodeMap attributes, Map<String, String> keyValues) {
        if (null == attributes || attributes.getLength() < 1) {
            return;
        }
        for (int i = 0; i < attributes.getLength(); i++) {
            Node node = attributes.item(i);
            if (null == node) {
                continue;
            }
            if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                if (StringUtils.isEmpty(node.getNodeName())) {
                    continue;
                }
                if (StringUtils.isEmpty(node.getNodeValue())) {
                    continue;
                }
                keyValues.put(String.join(StringUtility.DOT, path, node.getNodeName()), node.getNodeValue());
            }
        }
    }

    private void flatten(String path, NodeList nodes, Map<String, String> keyValues) {
        if (nodes == null || nodes.getLength() < 1) {
            return;
        }
        path = path == null ? "" : path;
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String value = node.getNodeValue();
            value = value == null ? "" : value.trim();
            String name = node.getNodeName();
            name = name == null ? "" : name.trim();
            if (StringUtils.isEmpty(name)) {
                continue;
            }
            String key = StringUtility.isEmpty(path) ? name : path + StringUtility.DOT + name;
            NamedNodeMap nodeMap = node.getAttributes();
            flatten(key, nodeMap, keyValues);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.hasChildNodes()) {
                flatten(key, node.getChildNodes(), keyValues);
                continue;
            }
            if (value.length() < 1) {
                continue;
            }
            keyValues.put(path, value);
        }
    }

    public XmlConfigurator(String xml) {
        this.keyValues = new LinkedHashMap<>();
        xml = xml.replaceAll("\\r", "").replaceAll("\\n", "").replaceAll("\\t", "");
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(xml)));
            flatten("", document.getChildNodes(), keyValues);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public String getString(String name) {
        return keyValues.get(name);
    }

    @Override
    public Collection<String> getKeys() {
        return keyValues.keySet();
    }

}
