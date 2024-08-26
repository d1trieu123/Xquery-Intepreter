// Generated from com/cse232/app/XPath.g4 by ANTLR 4.13.1
package com.cse232.app;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link XPathParser}.
 */
public interface XPathListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link XPathParser#abspath}.
	 * @param ctx the parse tree
	 */
	void enterAbspath(XPathParser.AbspathContext ctx);
	/**
	 * Exit a parse tree produced by {@link XPathParser#abspath}.
	 * @param ctx the parse tree
	 */
	void exitAbspath(XPathParser.AbspathContext ctx);
	/**
	 * Enter a parse tree produced by {@link XPathParser#doc}.
	 * @param ctx the parse tree
	 */
	void enterDoc(XPathParser.DocContext ctx);
	/**
	 * Exit a parse tree produced by {@link XPathParser#doc}.
	 * @param ctx the parse tree
	 */
	void exitDoc(XPathParser.DocContext ctx);
	/**
	 * Enter a parse tree produced by {@link XPathParser#relpath}.
	 * @param ctx the parse tree
	 */
	void enterRelpath(XPathParser.RelpathContext ctx);
	/**
	 * Exit a parse tree produced by {@link XPathParser#relpath}.
	 * @param ctx the parse tree
	 */
	void exitRelpath(XPathParser.RelpathContext ctx);
	/**
	 * Enter a parse tree produced by {@link XPathParser#filter}.
	 * @param ctx the parse tree
	 */
	void enterFilter(XPathParser.FilterContext ctx);
	/**
	 * Exit a parse tree produced by {@link XPathParser#filter}.
	 * @param ctx the parse tree
	 */
	void exitFilter(XPathParser.FilterContext ctx);
	/**
	 * Enter a parse tree produced by {@link XPathParser#filename}.
	 * @param ctx the parse tree
	 */
	void enterFilename(XPathParser.FilenameContext ctx);
	/**
	 * Exit a parse tree produced by {@link XPathParser#filename}.
	 * @param ctx the parse tree
	 */
	void exitFilename(XPathParser.FilenameContext ctx);
	/**
	 * Enter a parse tree produced by {@link XPathParser#tagname}.
	 * @param ctx the parse tree
	 */
	void enterTagname(XPathParser.TagnameContext ctx);
	/**
	 * Exit a parse tree produced by {@link XPathParser#tagname}.
	 * @param ctx the parse tree
	 */
	void exitTagname(XPathParser.TagnameContext ctx);
	/**
	 * Enter a parse tree produced by {@link XPathParser#strconst}.
	 * @param ctx the parse tree
	 */
	void enterStrconst(XPathParser.StrconstContext ctx);
	/**
	 * Exit a parse tree produced by {@link XPathParser#strconst}.
	 * @param ctx the parse tree
	 */
	void exitStrconst(XPathParser.StrconstContext ctx);
}