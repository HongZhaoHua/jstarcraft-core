package com.jstarcraft.core.antlr;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({ "all", "warnings", "unchecked", "unused", "cast" })
public class CalculatorLexer extends Lexer {
    static {
        RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache = new PredictionContextCache();
    public static final int Add = 1, Subtract = 2, Multiply = 3, Divide = 4, Number = 5, Open = 6, Close = 7, Space = 8;
    public static String[] channelNames = { "DEFAULT_TOKEN_CHANNEL", "HIDDEN" };

    public static String[] modeNames = { "DEFAULT_MODE" };

    private static String[] makeRuleNames() {
        return new String[] { "Add", "Subtract", "Multiply", "Divide", "Number", "Open", "Close", "Space" };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[] { null, null, null, null, null, null, "'('", "')'" };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[] { null, "Add", "Subtract", "Multiply", "Divide", "Number", "Open", "Close", "Space" };
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

    public CalculatorLexer(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @Override
    public String getGrammarFileName() {
        return "Calculator.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public String[] getChannelNames() {
        return channelNames;
    }

    @Override
    public String[] getModeNames() {
        return modeNames;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public static final String _serializedATN = "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\nI\b\1\4\2\t\2\4" + "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\2\3\2" + "\3\2\5\2\31\n\2\3\3\3\3\3\3\3\3\3\3\3\3\5\3!\n\3\3\4\3\4\3\4\3\4\3\4\3" + "\4\3\4\3\4\3\4\5\4,\n\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5\65\n\5\3\6\5\6" + "8\n\6\3\6\6\6;\n\6\r\6\16\6<\3\7\3\7\3\b\3\b\3\t\6\tD\n\t\r\t\16\tE\3" + "\t\3\t\2\2\n\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\3\2\4\3\2\62;\5\2\13\f" + "\17\17\"\"\2O\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2" + "\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\3\30\3\2\2\2\5 \3\2\2\2\7+" + "\3\2\2\2\t\64\3\2\2\2\13\67\3\2\2\2\r>\3\2\2\2\17@\3\2\2\2\21C\3\2\2\2" + "\23\24\7r\2\2\24\25\7n\2\2\25\26\7w\2\2\26\31\7u\2\2\27\31\7-\2\2\30\23" + "\3\2\2\2\30\27\3\2\2\2\31\4\3\2\2\2\32\33\7o\2\2\33\34\7k\2\2\34\35\7" + "p\2\2\35\36\7w\2\2\36!\7u\2\2\37!\7/\2\2 \32\3\2\2\2 \37\3\2\2\2!\6\3"
            + "\2\2\2\"#\7o\2\2#$\7w\2\2$%\7n\2\2%&\7v\2\2&\'\7k\2\2\'(\7r\2\2()\7n\2" + "\2),\7{\2\2*,\7,\2\2+\"\3\2\2\2+*\3\2\2\2,\b\3\2\2\2-.\7f\2\2./\7k\2\2" + "/\60\7x\2\2\60\61\7k\2\2\61\62\7f\2\2\62\65\7g\2\2\63\65\7\61\2\2\64-" + "\3\2\2\2\64\63\3\2\2\2\65\n\3\2\2\2\668\7/\2\2\67\66\3\2\2\2\678\3\2\2" + "\28:\3\2\2\29;\t\2\2\2:9\3\2\2\2;<\3\2\2\2<:\3\2\2\2<=\3\2\2\2=\f\3\2" + "\2\2>?\7*\2\2?\16\3\2\2\2@A\7+\2\2A\20\3\2\2\2BD\t\3\2\2CB\3\2\2\2DE\3" + "\2\2\2EC\3\2\2\2EF\3\2\2\2FG\3\2\2\2GH\b\t\2\2H\22\3\2\2\2\n\2\30 +\64" + "\67<E\3\b\2\2";
    public static final ATN _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}