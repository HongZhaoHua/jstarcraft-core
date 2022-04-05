package com.jstarcraft.core.common.selection.xpath;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.seimicrawler.xpath.JXNode;
import org.seimicrawler.xpath.antlr.XpathLexer;
import org.seimicrawler.xpath.antlr.XpathParser;
import org.seimicrawler.xpath.core.XValue;
import org.seimicrawler.xpath.core.XpathProcessor;
import org.seimicrawler.xpath.exception.DoFailOnErrorHandler;
import org.seimicrawler.xpath.exception.XpathSyntaxErrorException;

/**
 * XPath选择器
 * 
 * <pre>
 * 基于JsoupXpath
 * </pre>
 * 
 * @author Birdy
 */
public class SeimiXpathSelector extends XpathSelector<JXNode> {

    public SeimiXpathSelector(String query) {
        super(query);
    }

    @Override
    public List<JXNode> selectMultiple(JXNode content) {
        Elements elements = new Elements(content.asElement());
        try {
            CharStream stream = CharStreams.fromString(query);
            XpathLexer lexer = new XpathLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            XpathParser parser = new XpathParser(tokens);
            parser.setErrorHandler(new DoFailOnErrorHandler());
            ParseTree tree = parser.main();
            XpathProcessor processor = new XpathProcessor(elements);
            XValue value = processor.visit(tree);
            if (value == null) {
                return Collections.EMPTY_LIST;
            }
            List<JXNode> nodes = new LinkedList<>();
            if (value.isElements()) {
                for (Element element : value.asElements()) {
                    nodes.add(JXNode.create(element));
                }
                return nodes;
            }
            if (value.isList()) {
                for (String string : value.asList()) {
                    nodes.add(JXNode.create(string));
                }
                return nodes;
            }
            if (value.isString()) {
                nodes.add(JXNode.create(value.asString()));
                return nodes;
            }
            if (value.isNumber()) {
                Class<?> type = value.valType();
                if (type.isAssignableFrom(byte.class) || type.isAssignableFrom(short.class) || type.isAssignableFrom(int.class) || type.isAssignableFrom(long.class) || type.isAssignableFrom(Byte.class) || type.isAssignableFrom(Short.class) || type.isAssignableFrom(Integer.class) || type.isAssignableFrom(Long.class)) {
                    nodes.add(JXNode.create(value.asLong()));
                } else {
                    nodes.add(JXNode.create(value.asDouble()));
                }
                return nodes;
            }
            if (value.isBoolean()) {
                nodes.add(JXNode.create(value.asBoolean()));
                return nodes;
            }
            if (value.isDate()) {
                nodes.add(JXNode.create(value.asDate()));
                return nodes;
            }
            // TODO 此处考虑通过继承和反射支持其它类型
            nodes.add(JXNode.create(value.asString()));
            return nodes;
        } catch (Exception exception) {
            throw new XpathSyntaxErrorException("XPath查询异常", exception);
        }
    }

}
