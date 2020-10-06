// Generated from Type.g4 by ANTLR 4.8

package com.jstarcraft.core.common.reflection;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TypeParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TypeVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TypeParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray(TypeParser.ArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeParser#clazz}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClazz(TypeParser.ClazzContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeParser#generic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGeneric(TypeParser.GenericContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(TypeParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeParser#variable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable(TypeParser.VariableContext ctx);
	/**
	 * Visit a parse tree produced by {@link TypeParser#wildcard}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWildcard(TypeParser.WildcardContext ctx);
}