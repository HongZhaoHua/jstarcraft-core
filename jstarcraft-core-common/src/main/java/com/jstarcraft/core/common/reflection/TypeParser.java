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

@SuppressWarnings({ "all", "warnings", "unchecked", "unused", "cast" })
public class TypeParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache = new PredictionContextCache();
    public static final int T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, ARRAY = 6, BOUND = 7, ID = 8, SPACE = 9;
    public static final int RULE_array = 0, RULE_clazz = 1, RULE_generic = 2, RULE_type = 3, RULE_variable = 4, RULE_wildcard = 5;

    private static String[] makeRuleNames() {
        return new String[] { "array", "clazz", "generic", "type", "variable", "wildcard" };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[] { null, "'<'", "','", "'>'", "'&'", "'?'", "'[]'" };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[] { null, null, null, null, null, null, "ARRAY", "BOUND", "ID", "SPACE" };
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
    public String getGrammarFileName() {
        return "Type.g4";
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
    public ATN getATN() {
        return _ATN;
    }

    public TypeParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    public static class ArrayContext extends ParserRuleContext {
        public ClazzContext clazz() {
            return getRuleContext(ClazzContext.class, 0);
        }

        public List<TerminalNode> ARRAY() {
            return getTokens(TypeParser.ARRAY);
        }

        public TerminalNode ARRAY(int i) {
            return getToken(TypeParser.ARRAY, i);
        }

        public GenericContext generic() {
            return getRuleContext(GenericContext.class, 0);
        }

        public VariableContext variable() {
            return getRuleContext(VariableContext.class, 0);
        }

        public WildcardContext wildcard() {
            return getRuleContext(WildcardContext.class, 0);
        }

        public ArrayContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_array;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof TypeListener)
                ((TypeListener) listener).enterArray(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof TypeListener)
                ((TypeListener) listener).exitArray(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof TypeVisitor)
                return ((TypeVisitor<? extends T>) visitor).visitArray(this);
            else
                return visitor.visitChildren(this);
        }
    }

    public final ArrayContext array() throws RecognitionException {
        ArrayContext _localctx = new ArrayContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_array);
        try {
            int _alt;
            setState(36);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 4, _ctx)) {
            case 1:
                enterOuterAlt(_localctx, 1); {
                setState(12);
                clazz();
                setState(14);
                _errHandler.sync(this);
                _alt = 1;
                do {
                    switch (_alt) {
                    case 1: {
                        {
                            setState(13);
                            match(ARRAY);
                        }
                    }
                        break;
                    default:
                        throw new NoViableAltException(this);
                    }
                    setState(16);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 0, _ctx);
                } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
            }
                break;
            case 2:
                enterOuterAlt(_localctx, 2); {
                setState(18);
                generic();
                setState(20);
                _errHandler.sync(this);
                _alt = 1;
                do {
                    switch (_alt) {
                    case 1: {
                        {
                            setState(19);
                            match(ARRAY);
                        }
                    }
                        break;
                    default:
                        throw new NoViableAltException(this);
                    }
                    setState(22);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
                } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
            }
                break;
            case 3:
                enterOuterAlt(_localctx, 3); {
                setState(24);
                variable();
                setState(26);
                _errHandler.sync(this);
                _alt = 1;
                do {
                    switch (_alt) {
                    case 1: {
                        {
                            setState(25);
                            match(ARRAY);
                        }
                    }
                        break;
                    default:
                        throw new NoViableAltException(this);
                    }
                    setState(28);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 2, _ctx);
                } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
            }
                break;
            case 4:
                enterOuterAlt(_localctx, 4); {
                setState(30);
                wildcard();
                setState(32);
                _errHandler.sync(this);
                _alt = 1;
                do {
                    switch (_alt) {
                    case 1: {
                        {
                            setState(31);
                            match(ARRAY);
                        }
                    }
                        break;
                    default:
                        throw new NoViableAltException(this);
                    }
                    setState(34);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 3, _ctx);
                } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
            }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ClazzContext extends ParserRuleContext {
        public TerminalNode ID() {
            return getToken(TypeParser.ID, 0);
        }

        public ClazzContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_clazz;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof TypeListener)
                ((TypeListener) listener).enterClazz(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof TypeListener)
                ((TypeListener) listener).exitClazz(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof TypeVisitor)
                return ((TypeVisitor<? extends T>) visitor).visitClazz(this);
            else
                return visitor.visitChildren(this);
        }
    }

    public final ClazzContext clazz() throws RecognitionException {
        ClazzContext _localctx = new ClazzContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_clazz);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(38);
                match(ID);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class GenericContext extends ParserRuleContext {
        public ClazzContext clazz() {
            return getRuleContext(ClazzContext.class, 0);
        }

        public List<TypeContext> type() {
            return getRuleContexts(TypeContext.class);
        }

        public TypeContext type(int i) {
            return getRuleContext(TypeContext.class, i);
        }

        public GenericContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_generic;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof TypeListener)
                ((TypeListener) listener).enterGeneric(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof TypeListener)
                ((TypeListener) listener).exitGeneric(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof TypeVisitor)
                return ((TypeVisitor<? extends T>) visitor).visitGeneric(this);
            else
                return visitor.visitChildren(this);
        }
    }

    public final GenericContext generic() throws RecognitionException {
        GenericContext _localctx = new GenericContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_generic);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(40);
                clazz();
                setState(41);
                match(T__0);
                setState(42);
                type();
                setState(47);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == T__1) {
                    {
                        {
                            setState(43);
                            match(T__1);
                            setState(44);
                            type();
                        }
                    }
                    setState(49);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(50);
                match(T__2);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class TypeContext extends ParserRuleContext {
        public ArrayContext array() {
            return getRuleContext(ArrayContext.class, 0);
        }

        public ClazzContext clazz() {
            return getRuleContext(ClazzContext.class, 0);
        }

        public GenericContext generic() {
            return getRuleContext(GenericContext.class, 0);
        }

        public VariableContext variable() {
            return getRuleContext(VariableContext.class, 0);
        }

        public WildcardContext wildcard() {
            return getRuleContext(WildcardContext.class, 0);
        }

        public TypeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_type;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof TypeListener)
                ((TypeListener) listener).enterType(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof TypeListener)
                ((TypeListener) listener).exitType(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof TypeVisitor)
                return ((TypeVisitor<? extends T>) visitor).visitType(this);
            else
                return visitor.visitChildren(this);
        }
    }

    public final TypeContext type() throws RecognitionException {
        TypeContext _localctx = new TypeContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_type);
        try {
            setState(57);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 6, _ctx)) {
            case 1:
                enterOuterAlt(_localctx, 1); {
                setState(52);
                array();
            }
                break;
            case 2:
                enterOuterAlt(_localctx, 2); {
                setState(53);
                clazz();
            }
                break;
            case 3:
                enterOuterAlt(_localctx, 3); {
                setState(54);
                generic();
            }
                break;
            case 4:
                enterOuterAlt(_localctx, 4); {
                setState(55);
                variable();
            }
                break;
            case 5:
                enterOuterAlt(_localctx, 5); {
                setState(56);
                wildcard();
            }
                break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class VariableContext extends ParserRuleContext {
        public TerminalNode ID() {
            return getToken(TypeParser.ID, 0);
        }

        public TerminalNode BOUND() {
            return getToken(TypeParser.BOUND, 0);
        }

        public List<GenericContext> generic() {
            return getRuleContexts(GenericContext.class);
        }

        public GenericContext generic(int i) {
            return getRuleContext(GenericContext.class, i);
        }

        public VariableContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_variable;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof TypeListener)
                ((TypeListener) listener).enterVariable(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof TypeListener)
                ((TypeListener) listener).exitVariable(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof TypeVisitor)
                return ((TypeVisitor<? extends T>) visitor).visitVariable(this);
            else
                return visitor.visitChildren(this);
        }
    }

    public final VariableContext variable() throws RecognitionException {
        VariableContext _localctx = new VariableContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_variable);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(59);
                match(ID);
                setState(69);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BOUND) {
                    {
                        setState(60);
                        match(BOUND);
                        setState(61);
                        generic();
                        setState(66);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == T__3) {
                            {
                                {
                                    setState(62);
                                    match(T__3);
                                    setState(63);
                                    generic();
                                }
                            }
                            setState(68);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                }

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class WildcardContext extends ParserRuleContext {
        public TerminalNode BOUND() {
            return getToken(TypeParser.BOUND, 0);
        }

        public TypeContext type() {
            return getRuleContext(TypeContext.class, 0);
        }

        public WildcardContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_wildcard;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof TypeListener)
                ((TypeListener) listener).enterWildcard(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof TypeListener)
                ((TypeListener) listener).exitWildcard(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof TypeVisitor)
                return ((TypeVisitor<? extends T>) visitor).visitWildcard(this);
            else
                return visitor.visitChildren(this);
        }
    }

    public final WildcardContext wildcard() throws RecognitionException {
        WildcardContext _localctx = new WildcardContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_wildcard);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(71);
                match(T__4);
                setState(74);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BOUND) {
                    {
                        setState(72);
                        match(BOUND);
                        setState(73);
                        type();
                    }
                }

            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static final String _serializedATN = "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\13O\4\2\t\2\4\3\t" + "\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\6\2\21\n\2\r\2\16\2\22\3\2" + "\3\2\6\2\27\n\2\r\2\16\2\30\3\2\3\2\6\2\35\n\2\r\2\16\2\36\3\2\3\2\6\2" + "#\n\2\r\2\16\2$\5\2\'\n\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\7\4\60\n\4\f\4\16" + "\4\63\13\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\5\5<\n\5\3\6\3\6\3\6\3\6\3\6\7" + "\6C\n\6\f\6\16\6F\13\6\5\6H\n\6\3\7\3\7\3\7\5\7M\n\7\3\7\2\2\b\2\4\6\b" + "\n\f\2\2\2W\2&\3\2\2\2\4(\3\2\2\2\6*\3\2\2\2\b;\3\2\2\2\n=\3\2\2\2\fI" + "\3\2\2\2\16\20\5\4\3\2\17\21\7\b\2\2\20\17\3\2\2\2\21\22\3\2\2\2\22\20" + "\3\2\2\2\22\23\3\2\2\2\23\'\3\2\2\2\24\26\5\6\4\2\25\27\7\b\2\2\26\25" + "\3\2\2\2\27\30\3\2\2\2\30\26\3\2\2\2\30\31\3\2\2\2\31\'\3\2\2\2\32\34" + "\5\n\6\2\33\35\7\b\2\2\34\33\3\2\2\2\35\36\3\2\2\2\36\34\3\2\2\2\36\37" + "\3\2\2\2\37\'\3\2\2\2 \"\5\f\7\2!#\7\b\2\2\"!\3\2\2\2#$\3\2\2\2$\"\3\2"
            + "\2\2$%\3\2\2\2%\'\3\2\2\2&\16\3\2\2\2&\24\3\2\2\2&\32\3\2\2\2& \3\2\2" + "\2\'\3\3\2\2\2()\7\n\2\2)\5\3\2\2\2*+\5\4\3\2+,\7\3\2\2,\61\5\b\5\2-." + "\7\4\2\2.\60\5\b\5\2/-\3\2\2\2\60\63\3\2\2\2\61/\3\2\2\2\61\62\3\2\2\2" + "\62\64\3\2\2\2\63\61\3\2\2\2\64\65\7\5\2\2\65\7\3\2\2\2\66<\5\2\2\2\67" + "<\5\4\3\28<\5\6\4\29<\5\n\6\2:<\5\f\7\2;\66\3\2\2\2;\67\3\2\2\2;8\3\2" + "\2\2;9\3\2\2\2;:\3\2\2\2<\t\3\2\2\2=G\7\n\2\2>?\7\t\2\2?D\5\6\4\2@A\7" + "\6\2\2AC\5\6\4\2B@\3\2\2\2CF\3\2\2\2DB\3\2\2\2DE\3\2\2\2EH\3\2\2\2FD\3" + "\2\2\2G>\3\2\2\2GH\3\2\2\2H\13\3\2\2\2IL\7\7\2\2JK\7\t\2\2KM\5\b\5\2L" + "J\3\2\2\2LM\3\2\2\2M\r\3\2\2\2\f\22\30\36$&\61;DGL";
    public static final ATN _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}