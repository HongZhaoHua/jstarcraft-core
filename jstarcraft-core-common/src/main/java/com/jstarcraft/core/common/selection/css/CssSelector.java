package com.jstarcraft.core.common.selection.css;

import java.util.List;

import org.jsoup.nodes.Element;
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
 */
public class CssSelector extends AbstractSelector<Element> {

    public CssSelector(String query) {
        super(query);
    }

    @Override
    public List<Element> selectContent(Element content) {
        return Selector.select(query, content);
    }

}
