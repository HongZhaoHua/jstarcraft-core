// Generated from Type.g4 by ANTLR 4.8

package com.jstarcraft.core.common.reflection;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TypeParser}.
 */
public interface TypeListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TypeParser#array}.
	 * @param ctx the parse tree
	 */
	void enterArray(TypeParser.ArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeParser#array}.
	 * @param ctx the parse tree
	 */
	void exitArray(TypeParser.ArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeParser#clazz}.
	 * @param ctx the parse tree
	 */
	void enterClazz(TypeParser.ClazzContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeParser#clazz}.
	 * @param ctx the parse tree
	 */
	void exitClazz(TypeParser.ClazzContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeParser#generic}.
	 * @param ctx the parse tree
	 */
	void enterGeneric(TypeParser.GenericContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeParser#generic}.
	 * @param ctx the parse tree
	 */
	void exitGeneric(TypeParser.GenericContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(TypeParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(TypeParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeParser#variable}.
	 * @param ctx the parse tree
	 */
	void enterVariable(TypeParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeParser#variable}.
	 * @param ctx the parse tree
	 */
	void exitVariable(TypeParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by {@link TypeParser#wildcard}.
	 * @param ctx the parse tree
	 */
	void enterWildcard(TypeParser.WildcardContext ctx);
	/**
	 * Exit a parse tree produced by {@link TypeParser#wildcard}.
	 * @param ctx the parse tree
	 */
	void exitWildcard(TypeParser.WildcardContext ctx);
}