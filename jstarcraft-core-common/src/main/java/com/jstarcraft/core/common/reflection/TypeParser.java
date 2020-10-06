// Generated from Type.g4 by ANTLR 4.8

package com.jstarcraft.core.common.reflection;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TypeParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, ARRAY=6, BOUND=7, GENERIC=8, ID=9, 
		SPACE=10;
	public static final int
		RULE_array = 0, RULE_clazz = 1, RULE_generic = 2, RULE_type = 3, RULE_variable = 4, 
		RULE_wildcard = 5;
	private static String[] makeRuleNames() {
		return new String[] {
			"array", "clazz", "generic", "type", "variable", "wildcard"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'<'", "','", "'>'", "'&'", "'?'", "'[]'", null, "'<>'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, "ARRAY", "BOUND", "GENERIC", "ID", 
			"SPACE"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Type.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TypeParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ArrayContext extends ParserRuleContext {
		public ClazzContext clazz() {
			return getRuleContext(ClazzContext.class,0);
		}
		public List<TerminalNode> GENERIC() { return getTokens(TypeParser.GENERIC); }
		public TerminalNode GENERIC(int i) {
			return getToken(TypeParser.GENERIC, i);
		}
		public List<TerminalNode> ARRAY() { return getTokens(TypeParser.ARRAY); }
		public TerminalNode ARRAY(int i) {
			return getToken(TypeParser.ARRAY, i);
		}
		public GenericContext generic() {
			return getRuleContext(GenericContext.class,0);
		}
		public ArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeListener ) ((TypeListener)listener).enterArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeListener ) ((TypeListener)listener).exitArray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeVisitor ) return ((TypeVisitor<? extends T>)visitor).visitArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayContext array() throws RecognitionException {
		ArrayContext _localctx = new ArrayContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_array);
		int _la;
		try {
			setState(50);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(12);
				clazz();
				setState(29);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case ARRAY:
					{
					{
					setState(14); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(13);
						match(ARRAY);
						}
						}
						setState(16); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==ARRAY );
					setState(21);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==GENERIC) {
						{
						{
						setState(18);
						match(GENERIC);
						}
						}
						setState(23);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					}
					break;
				case GENERIC:
					{
					setState(25); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(24);
						match(GENERIC);
						}
						}
						setState(27); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==GENERIC );
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(31);
				generic();
				setState(48);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case ARRAY:
					{
					{
					setState(33); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(32);
						match(ARRAY);
						}
						}
						setState(35); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==ARRAY );
					setState(40);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==GENERIC) {
						{
						{
						setState(37);
						match(GENERIC);
						}
						}
						setState(42);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
					}
					break;
				case GENERIC:
					{
					setState(44); 
					_errHandler.sync(this);
					_la = _input.LA(1);
					do {
						{
						{
						setState(43);
						match(GENERIC);
						}
						}
						setState(46); 
						_errHandler.sync(this);
						_la = _input.LA(1);
					} while ( _la==GENERIC );
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ClazzContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(TypeParser.ID, 0); }
		public ClazzContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_clazz; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeListener ) ((TypeListener)listener).enterClazz(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeListener ) ((TypeListener)listener).exitClazz(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeVisitor ) return ((TypeVisitor<? extends T>)visitor).visitClazz(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClazzContext clazz() throws RecognitionException {
		ClazzContext _localctx = new ClazzContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_clazz);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GenericContext extends ParserRuleContext {
		public ClazzContext clazz() {
			return getRuleContext(ClazzContext.class,0);
		}
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public GenericContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_generic; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeListener ) ((TypeListener)listener).enterGeneric(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeListener ) ((TypeListener)listener).exitGeneric(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeVisitor ) return ((TypeVisitor<? extends T>)visitor).visitGeneric(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GenericContext generic() throws RecognitionException {
		GenericContext _localctx = new GenericContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_generic);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(54);
			clazz();
			setState(55);
			match(T__0);
			setState(56);
			type();
			setState(61);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(57);
				match(T__1);
				setState(58);
				type();
				}
				}
				setState(63);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(64);
			match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TypeContext extends ParserRuleContext {
		public WildcardContext wildcard() {
			return getRuleContext(WildcardContext.class,0);
		}
		public ArrayContext array() {
			return getRuleContext(ArrayContext.class,0);
		}
		public ClazzContext clazz() {
			return getRuleContext(ClazzContext.class,0);
		}
		public GenericContext generic() {
			return getRuleContext(GenericContext.class,0);
		}
		public VariableContext variable() {
			return getRuleContext(VariableContext.class,0);
		}
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeListener ) ((TypeListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeListener ) ((TypeListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeVisitor ) return ((TypeVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_type);
		try {
			setState(71);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(66);
				wildcard();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(67);
				array();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(68);
				clazz();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(69);
				generic();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(70);
				variable();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(TypeParser.ID, 0); }
		public TerminalNode BOUND() { return getToken(TypeParser.BOUND, 0); }
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public VariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeListener ) ((TypeListener)listener).enterVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeListener ) ((TypeListener)listener).exitVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeVisitor ) return ((TypeVisitor<? extends T>)visitor).visitVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableContext variable() throws RecognitionException {
		VariableContext _localctx = new VariableContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_variable);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(73);
			match(ID);
			setState(83);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BOUND) {
				{
				setState(74);
				match(BOUND);
				{
				setState(75);
				type();
				}
				setState(80);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(76);
						match(T__3);
						{
						setState(77);
						type();
						}
						}
						} 
					}
					setState(82);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class WildcardContext extends ParserRuleContext {
		public TerminalNode BOUND() { return getToken(TypeParser.BOUND, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public WildcardContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_wildcard; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TypeListener ) ((TypeListener)listener).enterWildcard(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TypeListener ) ((TypeListener)listener).exitWildcard(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TypeVisitor ) return ((TypeVisitor<? extends T>)visitor).visitWildcard(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WildcardContext wildcard() throws RecognitionException {
		WildcardContext _localctx = new WildcardContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_wildcard);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			match(T__4);
			setState(88);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BOUND) {
				{
				setState(86);
				match(BOUND);
				setState(87);
				type();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\f]\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\6\2\21\n\2\r\2\16\2\22\3\2"+
		"\7\2\26\n\2\f\2\16\2\31\13\2\3\2\6\2\34\n\2\r\2\16\2\35\5\2 \n\2\3\2\3"+
		"\2\6\2$\n\2\r\2\16\2%\3\2\7\2)\n\2\f\2\16\2,\13\2\3\2\6\2/\n\2\r\2\16"+
		"\2\60\5\2\63\n\2\5\2\65\n\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\7\4>\n\4\f\4\16"+
		"\4A\13\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\5\5J\n\5\3\6\3\6\3\6\3\6\3\6\7\6"+
		"Q\n\6\f\6\16\6T\13\6\5\6V\n\6\3\7\3\7\3\7\5\7[\n\7\3\7\2\2\b\2\4\6\b\n"+
		"\f\2\2\2g\2\64\3\2\2\2\4\66\3\2\2\2\68\3\2\2\2\bI\3\2\2\2\nK\3\2\2\2\f"+
		"W\3\2\2\2\16\37\5\4\3\2\17\21\7\b\2\2\20\17\3\2\2\2\21\22\3\2\2\2\22\20"+
		"\3\2\2\2\22\23\3\2\2\2\23\27\3\2\2\2\24\26\7\n\2\2\25\24\3\2\2\2\26\31"+
		"\3\2\2\2\27\25\3\2\2\2\27\30\3\2\2\2\30 \3\2\2\2\31\27\3\2\2\2\32\34\7"+
		"\n\2\2\33\32\3\2\2\2\34\35\3\2\2\2\35\33\3\2\2\2\35\36\3\2\2\2\36 \3\2"+
		"\2\2\37\20\3\2\2\2\37\33\3\2\2\2 \65\3\2\2\2!\62\5\6\4\2\"$\7\b\2\2#\""+
		"\3\2\2\2$%\3\2\2\2%#\3\2\2\2%&\3\2\2\2&*\3\2\2\2\')\7\n\2\2(\'\3\2\2\2"+
		"),\3\2\2\2*(\3\2\2\2*+\3\2\2\2+\63\3\2\2\2,*\3\2\2\2-/\7\n\2\2.-\3\2\2"+
		"\2/\60\3\2\2\2\60.\3\2\2\2\60\61\3\2\2\2\61\63\3\2\2\2\62#\3\2\2\2\62"+
		".\3\2\2\2\63\65\3\2\2\2\64\16\3\2\2\2\64!\3\2\2\2\65\3\3\2\2\2\66\67\7"+
		"\13\2\2\67\5\3\2\2\289\5\4\3\29:\7\3\2\2:?\5\b\5\2;<\7\4\2\2<>\5\b\5\2"+
		"=;\3\2\2\2>A\3\2\2\2?=\3\2\2\2?@\3\2\2\2@B\3\2\2\2A?\3\2\2\2BC\7\5\2\2"+
		"C\7\3\2\2\2DJ\5\f\7\2EJ\5\2\2\2FJ\5\4\3\2GJ\5\6\4\2HJ\5\n\6\2ID\3\2\2"+
		"\2IE\3\2\2\2IF\3\2\2\2IG\3\2\2\2IH\3\2\2\2J\t\3\2\2\2KU\7\13\2\2LM\7\t"+
		"\2\2MR\5\b\5\2NO\7\6\2\2OQ\5\b\5\2PN\3\2\2\2QT\3\2\2\2RP\3\2\2\2RS\3\2"+
		"\2\2SV\3\2\2\2TR\3\2\2\2UL\3\2\2\2UV\3\2\2\2V\13\3\2\2\2WZ\7\7\2\2XY\7"+
		"\t\2\2Y[\5\b\5\2ZX\3\2\2\2Z[\3\2\2\2[\r\3\2\2\2\20\22\27\35\37%*\60\62"+
		"\64?IRUZ";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}