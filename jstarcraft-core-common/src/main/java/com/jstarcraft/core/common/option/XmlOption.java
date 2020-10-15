package com.jstarcraft.core.common.option;

import java.io.StringReader;
import java.util.Iterator;
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

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * XML配置器
 * 
 * @author Birdy
 *
 */
public class XmlOption implements StringOption {

    /** 配置项 */
    private Map<String, String> keyValues;

    private void flatten(String path, NamedNodeMap attributes, Map<String, String> keyValues) {
        if (null == attributes || attributes.getLength() < 1) {
            return;
        }
        for (int index = 0, size = attributes.getLength(); index < size; index++) {
            Node node = attributes.item(index);
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
        int size = nodes.getLength();
        Object2IntOpenHashMap<String> counts = new Object2IntOpenHashMap<>();
        for (int index = 0; index < size; index++) {
            Node node = nodes.item(index);
            String name = node.getNodeName();
            int count = counts.getOrDefault(name, 0);
            counts.put(name, count + 1);
        }
        Object2IntOpenHashMap<String> currents = new Object2IntOpenHashMap<>();
        for (int index = 0; index < size; index++) {
            Node node = nodes.item(index);
            String name = node.getNodeName();
            name = name == null ? "" : name.trim();
            if (StringUtility.isEmpty(name)) {
                continue;
            }
            String value = node.getNodeValue();
            value = value == null ? "" : value.trim();
            if (counts.getInt(name) > 1) {
                int current = currents.getOrDefault(name, 0);
                currents.put(name, current + 1);
                name = name + "[" + current + "]";
            }
            String key = StringUtility.isEmpty(path) ? name : path + StringUtility.DOT + name;
            NamedNodeMap attributes = node.getAttributes();
            flatten(key, attributes, keyValues);
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

    public XmlOption(String xml) {
        this.keyValues = new LinkedHashMap<>();
        xml = xml.replaceAll("\\r", "").replaceAll("\\n", "").replaceAll("\\t", "");
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(new InputSource(new StringReader(xml)));
            flatten("", document.getAttributes(), keyValues);
            flatten("", document.getChildNodes(), keyValues);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public String getString(String key) {
        return keyValues.get(key);
    }

    @Override
    public Iterator<String> getKeys() {
        return keyValues.keySet().iterator();
    }

}
