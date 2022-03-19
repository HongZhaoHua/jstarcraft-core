package com.jstarcraft.core.common.selection.css;

import java.util.Collection;
import java.util.List;

import com.jstarcraft.core.common.selection.AbstractSelector;

import jodd.csselly.CSSelly;
import jodd.csselly.CssSelector;
import jodd.lagarto.dom.Node;
import jodd.lagarto.dom.NodeSelector;

/**
 * CSS选择器
 * 
 * <pre>
 * 基于Jodd
 * </pre>
 * 
 * @author Birdy
 *
 * @param <T>
 */
public class JoddCssSelector extends AbstractSelector<Node> {

    private Collection<List<CssSelector>> css;

    public JoddCssSelector(String query) {
        super(query);
        this.css = CSSelly.parse(query);
    }

    @Override
    public List<Node> selectMultiple(Node content) {
        NodeSelector selector = new NodeSelector(content);
        return selector.select(css);
    }

}
