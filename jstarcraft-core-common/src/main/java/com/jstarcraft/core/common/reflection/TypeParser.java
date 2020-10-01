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
    public static final int T__0 = 1, T__1 = 2, T__2 = 3, T__3 = 4, T__4 = 5, ARRAY = 6, BOUND = 7, GENERIC = 8, ID = 9, SPACE = 10;
    public static final int RULE_array = 0, RULE_clazz = 1, RULE_generic = 2, RULE_type = 3, RULE_variable = 4, RULE_wildcard = 5;

    private static String[] makeRuleNames() {
        return new String[] { "array", "clazz", "generic", "type", "variable", "wildcard" };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[] { null, "'<'", "','", "'>'", "'&'", "'?'", "'[]'", null, "'<>'" };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[] { null, null, null, null, null, null, "ARRAY", "BOUND", "GENERIC", "ID", "SPACE" };
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

        public List<TerminalNode> GENERIC() {
            return getTokens(TypeParser.GENERIC);
        }

        public TerminalNode GENERIC(int i) {
            return getToken(TypeParser.GENERIC, i);
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
            setState(88);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 16, _ctx)) {
            case 1:
                enterOuterAlt(_localctx, 1); {
                setState(12);
                clazz();
                setState(29);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                case ARRAY: {
                    {
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
                        setState(21);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
                        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                            if (_alt == 1) {
                                {
                                    {
                                        setState(18);
                                        match(GENERIC);
                                    }
                                }
                            }
                            setState(23);
                            _errHandler.sync(this);
                            _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
                        }
                    }
                }
                    break;
                case GENERIC: {
                    setState(25);
                    _errHandler.sync(this);
                    _alt = 1;
                    do {
                        switch (_alt) {
                        case 1: {
                            {
                                setState(24);
                                match(GENERIC);
                            }
                        }
                            break;
                        default:
                            throw new NoViableAltException(this);
                        }
                        setState(27);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 2, _ctx);
                    } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
                }
                    break;
                default:
                    throw new NoViableAltException(this);
                }
            }
                break;
            case 2:
                enterOuterAlt(_localctx, 2); {
                setState(31);
                generic();
                setState(48);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                case ARRAY: {
                    {
                        setState(33);
                        _errHandler.sync(this);
                        _alt = 1;
                        do {
                            switch (_alt) {
                            case 1: {
                                {
                                    setState(32);
                                    match(ARRAY);
                                }
                            }
                                break;
                            default:
                                throw new NoViableAltException(this);
                            }
                            setState(35);
                            _errHandler.sync(this);
                            _alt = getInterpreter().adaptivePredict(_input, 4, _ctx);
                        } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
                        setState(40);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 5, _ctx);
                        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                            if (_alt == 1) {
                                {
                                    {
                                        setState(37);
                                        match(GENERIC);
                                    }
                                }
                            }
                            setState(42);
                            _errHandler.sync(this);
                            _alt = getInterpreter().adaptivePredict(_input, 5, _ctx);
                        }
                    }
                }
                    break;
                case GENERIC: {
                    setState(44);
                    _errHandler.sync(this);
                    _alt = 1;
                    do {
                        switch (_alt) {
                        case 1: {
                            {
                                setState(43);
                                match(GENERIC);
                            }
                        }
                            break;
                        default:
                            throw new NoViableAltException(this);
                        }
                        setState(46);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 6, _ctx);
                    } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
                }
                    break;
                default:
                    throw new NoViableAltException(this);
                }
            }
                break;
            case 3:
                enterOuterAlt(_localctx, 3); {
                setState(50);
                variable();
                setState(67);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                case ARRAY: {
                    {
                        setState(52);
                        _errHandler.sync(this);
                        _alt = 1;
                        do {
                            switch (_alt) {
                            case 1: {
                                {
                                    setState(51);
                                    match(ARRAY);
                                }
                            }
                                break;
                            default:
                                throw new NoViableAltException(this);
                            }
                            setState(54);
                            _errHandler.sync(this);
                            _alt = getInterpreter().adaptivePredict(_input, 8, _ctx);
                        } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
                        setState(59);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 9, _ctx);
                        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                            if (_alt == 1) {
                                {
                                    {
                                        setState(56);
                                        match(GENERIC);
                                    }
                                }
                            }
                            setState(61);
                            _errHandler.sync(this);
                            _alt = getInterpreter().adaptivePredict(_input, 9, _ctx);
                        }
                    }
                }
                    break;
                case GENERIC: {
                    setState(63);
                    _errHandler.sync(this);
                    _alt = 1;
                    do {
                        switch (_alt) {
                        case 1: {
                            {
                                setState(62);
                                match(GENERIC);
                            }
                        }
                            break;
                        default:
                            throw new NoViableAltException(this);
                        }
                        setState(65);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 10, _ctx);
                    } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
                }
                    break;
                default:
                    throw new NoViableAltException(this);
                }
            }
                break;
            case 4:
                enterOuterAlt(_localctx, 4); {
                setState(69);
                wildcard();
                setState(86);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                case ARRAY: {
                    {
                        setState(71);
                        _errHandler.sync(this);
                        _alt = 1;
                        do {
                            switch (_alt) {
                            case 1: {
                                {
                                    setState(70);
                                    match(ARRAY);
                                }
                            }
                                break;
                            default:
                                throw new NoViableAltException(this);
                            }
                            setState(73);
                            _errHandler.sync(this);
                            _alt = getInterpreter().adaptivePredict(_input, 12, _ctx);
                        } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
                        setState(78);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 13, _ctx);
                        while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                            if (_alt == 1) {
                                {
                                    {
                                        setState(75);
                                        match(GENERIC);
                                    }
                                }
                            }
                            setState(80);
                            _errHandler.sync(this);
                            _alt = getInterpreter().adaptivePredict(_input, 13, _ctx);
                        }
                    }
                }
                    break;
                case GENERIC: {
                    setState(82);
                    _errHandler.sync(this);
                    _alt = 1;
                    do {
                        switch (_alt) {
                        case 1: {
                            {
                                setState(81);
                                match(GENERIC);
                            }
                        }
                            break;
                        default:
                            throw new NoViableAltException(this);
                        }
                        setState(84);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 14, _ctx);
                    } while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER);
                }
                    break;
                default:
                    throw new NoViableAltException(this);
                }
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
                setState(90);
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
                setState(92);
                clazz();
                setState(93);
                match(T__0);
                setState(94);
                type();
                setState(99);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == T__1) {
                    {
                        {
                            setState(95);
                            match(T__1);
                            setState(96);
                            type();
                        }
                    }
                    setState(101);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(102);
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
            setState(109);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 18, _ctx)) {
            case 1:
                enterOuterAlt(_localctx, 1); {
                setState(104);
                array();
            }
                break;
            case 2:
                enterOuterAlt(_localctx, 2); {
                setState(105);
                clazz();
            }
                break;
            case 3:
                enterOuterAlt(_localctx, 3); {
                setState(106);
                generic();
            }
                break;
            case 4:
                enterOuterAlt(_localctx, 4); {
                setState(107);
                variable();
            }
                break;
            case 5:
                enterOuterAlt(_localctx, 5); {
                setState(108);
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
                setState(111);
                match(ID);
                setState(121);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BOUND) {
                    {
                        setState(112);
                        match(BOUND);
                        setState(113);
                        generic();
                        setState(118);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == T__3) {
                            {
                                {
                                    setState(114);
                                    match(T__3);
                                    setState(115);
                                    generic();
                                }
                            }
                            setState(120);
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
                setState(123);
                match(T__4);
                setState(126);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BOUND) {
                    {
                        setState(124);
                        match(BOUND);
                        setState(125);
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

    public static final String _serializedATN = "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\f\u0083\4\2\t\2\4" + "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\6\2\21\n\2\r\2\16\2\22" + "\3\2\7\2\26\n\2\f\2\16\2\31\13\2\3\2\6\2\34\n\2\r\2\16\2\35\5\2 \n\2\3" + "\2\3\2\6\2$\n\2\r\2\16\2%\3\2\7\2)\n\2\f\2\16\2,\13\2\3\2\6\2/\n\2\r\2" + "\16\2\60\5\2\63\n\2\3\2\3\2\6\2\67\n\2\r\2\16\28\3\2\7\2<\n\2\f\2\16\2" + "?\13\2\3\2\6\2B\n\2\r\2\16\2C\5\2F\n\2\3\2\3\2\6\2J\n\2\r\2\16\2K\3\2" + "\7\2O\n\2\f\2\16\2R\13\2\3\2\6\2U\n\2\r\2\16\2V\5\2Y\n\2\5\2[\n\2\3\3" + "\3\3\3\4\3\4\3\4\3\4\3\4\7\4d\n\4\f\4\16\4g\13\4\3\4\3\4\3\5\3\5\3\5\3" + "\5\3\5\5\5p\n\5\3\6\3\6\3\6\3\6\3\6\7\6w\n\6\f\6\16\6z\13\6\5\6|\n\6\3" + "\7\3\7\3\7\5\7\u0081\n\7\3\7\2\2\b\2\4\6\b\n\f\2\2\2\u0097\2Z\3\2\2\2" + "\4\\\3\2\2\2\6^\3\2\2\2\bo\3\2\2\2\nq\3\2\2\2\f}\3\2\2\2\16\37\5\4\3\2" + "\17\21\7\b\2\2\20\17\3\2\2\2\21\22\3\2\2\2\22\20\3\2\2\2\22\23\3\2\2\2"
            + "\23\27\3\2\2\2\24\26\7\n\2\2\25\24\3\2\2\2\26\31\3\2\2\2\27\25\3\2\2\2" + "\27\30\3\2\2\2\30 \3\2\2\2\31\27\3\2\2\2\32\34\7\n\2\2\33\32\3\2\2\2\34" + "\35\3\2\2\2\35\33\3\2\2\2\35\36\3\2\2\2\36 \3\2\2\2\37\20\3\2\2\2\37\33" + "\3\2\2\2 [\3\2\2\2!\62\5\6\4\2\"$\7\b\2\2#\"\3\2\2\2$%\3\2\2\2%#\3\2\2" + "\2%&\3\2\2\2&*\3\2\2\2\')\7\n\2\2(\'\3\2\2\2),\3\2\2\2*(\3\2\2\2*+\3\2" + "\2\2+\63\3\2\2\2,*\3\2\2\2-/\7\n\2\2.-\3\2\2\2/\60\3\2\2\2\60.\3\2\2\2" + "\60\61\3\2\2\2\61\63\3\2\2\2\62#\3\2\2\2\62.\3\2\2\2\63[\3\2\2\2\64E\5" + "\n\6\2\65\67\7\b\2\2\66\65\3\2\2\2\678\3\2\2\28\66\3\2\2\289\3\2\2\29" + "=\3\2\2\2:<\7\n\2\2;:\3\2\2\2<?\3\2\2\2=;\3\2\2\2=>\3\2\2\2>F\3\2\2\2" + "?=\3\2\2\2@B\7\n\2\2A@\3\2\2\2BC\3\2\2\2CA\3\2\2\2CD\3\2\2\2DF\3\2\2\2" + "E\66\3\2\2\2EA\3\2\2\2F[\3\2\2\2GX\5\f\7\2HJ\7\b\2\2IH\3\2\2\2JK\3\2\2" + "\2KI\3\2\2\2KL\3\2\2\2LP\3\2\2\2MO\7\n\2\2NM\3\2\2\2OR\3\2\2\2PN\3\2\2" + "\2PQ\3\2\2\2QY\3\2\2\2RP\3\2\2\2SU\7\n\2\2TS\3\2\2\2UV\3\2\2\2VT\3\2\2"
            + "\2VW\3\2\2\2WY\3\2\2\2XI\3\2\2\2XT\3\2\2\2Y[\3\2\2\2Z\16\3\2\2\2Z!\3\2" + "\2\2Z\64\3\2\2\2ZG\3\2\2\2[\3\3\2\2\2\\]\7\13\2\2]\5\3\2\2\2^_\5\4\3\2" + "_`\7\3\2\2`e\5\b\5\2ab\7\4\2\2bd\5\b\5\2ca\3\2\2\2dg\3\2\2\2ec\3\2\2\2" + "ef\3\2\2\2fh\3\2\2\2ge\3\2\2\2hi\7\5\2\2i\7\3\2\2\2jp\5\2\2\2kp\5\4\3" + "\2lp\5\6\4\2mp\5\n\6\2np\5\f\7\2oj\3\2\2\2ok\3\2\2\2ol\3\2\2\2om\3\2\2" + "\2on\3\2\2\2p\t\3\2\2\2q{\7\13\2\2rs\7\t\2\2sx\5\6\4\2tu\7\6\2\2uw\5\6" + "\4\2vt\3\2\2\2wz\3\2\2\2xv\3\2\2\2xy\3\2\2\2y|\3\2\2\2zx\3\2\2\2{r\3\2" + "\2\2{|\3\2\2\2|\13\3\2\2\2}\u0080\7\7\2\2~\177\7\t\2\2\177\u0081\5\b\5" + "\2\u0080~\3\2\2\2\u0080\u0081\3\2\2\2\u0081\r\3\2\2\2\30\22\27\35\37%" + "*\60\628=CEKPVXZeox{\u0080";
    public static final ATN _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}