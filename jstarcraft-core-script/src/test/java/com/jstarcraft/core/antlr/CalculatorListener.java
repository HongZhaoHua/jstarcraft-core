package com.jstarcraft.core.antlr;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CalculatorParser}.
 */
public interface CalculatorListener extends ParseTreeListener {
    /**
     * Enter a parse tree produced by {@link CalculatorParser#formula}.
     * 
     * @param ctx the parse tree
     */
    void enterFormula(CalculatorParser.FormulaContext ctx);

    /**
     * Exit a parse tree produced by {@link CalculatorParser#formula}.
     * 
     * @param ctx the parse tree
     */
    void exitFormula(CalculatorParser.FormulaContext ctx);

    /**
     * Enter a parse tree produced by {@link CalculatorParser#number}.
     * 
     * @param ctx the parse tree
     */
    void enterNumber(CalculatorParser.NumberContext ctx);

    /**
     * Exit a parse tree produced by {@link CalculatorParser#number}.
     * 
     * @param ctx the parse tree
     */
    void exitNumber(CalculatorParser.NumberContext ctx);
}