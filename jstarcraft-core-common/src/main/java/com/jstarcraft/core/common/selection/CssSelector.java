package com.jstarcraft.core.common.selection;

import java.util.Collection;

import org.jsoup.nodes.Element;
import org.jsoup.select.Selector;

/**
 * CSS选择器
 * 
 * @author Birdy
 *
 */
public class CssSelector extends AbstractSelector<Element> {

    public CssSelector(String query) {
        super(query);
    }

    @Override
    public Collection<Element> selectContent(Element content) {
        return Selector.select(query, content);
    }

}
