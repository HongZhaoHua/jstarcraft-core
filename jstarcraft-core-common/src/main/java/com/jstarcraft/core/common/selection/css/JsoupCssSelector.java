package com.jstarcraft.core.common.selection.css;

import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Evaluator;
import org.jsoup.select.QueryParser;
import org.jsoup.select.Selector;

import com.jstarcraft.core.common.selection.AbstractSelector;

/**
 * CSS选择器
 * 
 * <pre>
 * 基于jsoup
 * </pre>
 * 
 * @author Birdy
 *
 * @param <T>
 */
public class JsoupCssSelector extends AbstractSelector<Element> {

    private Evaluator css;
    
    public JsoupCssSelector(String query) {
        super(query);
        this.css = QueryParser.parse(query);
    }

    @Override
    public List<Element> selectMultiple(Element content) {
        return Selector.select(css, content);
    }

}
