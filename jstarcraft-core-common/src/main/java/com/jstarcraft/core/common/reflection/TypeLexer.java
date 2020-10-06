// Generated from Type.g4 by ANTLR 4.8

package com.jstarcraft.core.common.reflection;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TypeLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, ARRAY=6, BOUND=7, GENERIC=8, ID=9, 
		SPACE=10;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "ARRAY", "BOUND", "GENERIC", 
			"ID", "SPACE"
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


	public TypeLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Type.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\fC\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\b\61\n\b\3\t\3\t\3\t\3\n\3\n\7\n"+
		"8\n\n\f\n\16\n;\13\n\3\13\6\13>\n\13\r\13\16\13?\3\13\3\13\2\2\f\3\3\5"+
		"\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\3\2\5\4\2C\\c|\7\2\60\60\62;"+
		"C\\aac|\5\2\13\f\17\17\"\"\2E\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t"+
		"\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2"+
		"\2\2\25\3\2\2\2\3\27\3\2\2\2\5\31\3\2\2\2\7\33\3\2\2\2\t\35\3\2\2\2\13"+
		"\37\3\2\2\2\r!\3\2\2\2\17\60\3\2\2\2\21\62\3\2\2\2\23\65\3\2\2\2\25=\3"+
		"\2\2\2\27\30\7>\2\2\30\4\3\2\2\2\31\32\7.\2\2\32\6\3\2\2\2\33\34\7@\2"+
		"\2\34\b\3\2\2\2\35\36\7(\2\2\36\n\3\2\2\2\37 \7A\2\2 \f\3\2\2\2!\"\7]"+
		"\2\2\"#\7_\2\2#\16\3\2\2\2$%\7g\2\2%&\7z\2\2&\'\7v\2\2\'(\7g\2\2()\7p"+
		"\2\2)*\7f\2\2*\61\7u\2\2+,\7u\2\2,-\7w\2\2-.\7r\2\2./\7g\2\2/\61\7t\2"+
		"\2\60$\3\2\2\2\60+\3\2\2\2\61\20\3\2\2\2\62\63\7>\2\2\63\64\7@\2\2\64"+
		"\22\3\2\2\2\659\t\2\2\2\668\t\3\2\2\67\66\3\2\2\28;\3\2\2\29\67\3\2\2"+
		"\29:\3\2\2\2:\24\3\2\2\2;9\3\2\2\2<>\t\4\2\2=<\3\2\2\2>?\3\2\2\2?=\3\2"+
		"\2\2?@\3\2\2\2@A\3\2\2\2AB\b\13\2\2B\26\3\2\2\2\6\2\609?\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}