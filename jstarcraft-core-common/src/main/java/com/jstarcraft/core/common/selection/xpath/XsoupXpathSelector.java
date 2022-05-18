package com.jstarcraft.core.common.selection.xpath;

import java.util.List;

import org.jsoup.nodes.Element;
import org.seimicrawler.xpath.exception.XpathSyntaxErrorException;

import us.codecraft.xsoup.XPathEvaluator;
import us.codecraft.xsoup.Xsoup;

/**
 * XPath选择器
 * 
 * <pre>
 * 基于Xsoup
 * </pre>
 * 
 * @author Birdy
 */
public class XsoupXpathSelector extends XpathSelector<Element> {

    private XPathEvaluator evaluator;

    public XsoupXpathSelector(String query) {
        super(query);
        this.evaluator = Xsoup.compile(query);
    }

    @Override
    public List<Element> selectMultiple(Element element) {
        try {
            List<Element> elements = evaluator.evaluate(element).getElements();
            return elements;
        } catch (Exception exception) {
            throw new XpathSyntaxErrorException("XPath查询异常", exception);
        }
    }

}
