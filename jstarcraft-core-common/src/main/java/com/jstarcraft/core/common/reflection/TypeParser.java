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
        int _la;
        try {
            setState(24);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 2, _ctx)) {
            case 1:
                enterOuterAlt(_localctx, 1); {
                setState(12);
                clazz();
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
                } while (_la == ARRAY);
            }
                break;
            case 2:
                enterOuterAlt(_localctx, 2); {
                setState(18);
                generic();
                setState(20);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(19);
                            match(ARRAY);
                        }
                    }
                    setState(22);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while (_la == ARRAY);
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
                setState(26);
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
                setState(28);
                clazz();
                setState(29);
                match(T__0);
                setState(30);
                type();
                setState(35);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == T__1) {
                    {
                        {
                            setState(31);
                            match(T__1);
                            setState(32);
                            type();
                        }
                    }
                    setState(37);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(38);
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
        public WildcardContext wildcard() {
            return getRuleContext(WildcardContext.class, 0);
        }

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
            setState(45);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 4, _ctx)) {
            case 1:
                enterOuterAlt(_localctx, 1); {
                setState(40);
                wildcard();
            }
                break;
            case 2:
                enterOuterAlt(_localctx, 2); {
                setState(41);
                array();
            }
                break;
            case 3:
                enterOuterAlt(_localctx, 3); {
                setState(42);
                clazz();
            }
                break;
            case 4:
                enterOuterAlt(_localctx, 4); {
                setState(43);
                generic();
            }
                break;
            case 5:
                enterOuterAlt(_localctx, 5); {
                setState(44);
                variable();
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
                setState(47);
                match(ID);
                setState(57);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BOUND) {
                    {
                        setState(48);
                        match(BOUND);
                        setState(49);
                        generic();
                        setState(54);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == T__3) {
                            {
                                {
                                    setState(50);
                                    match(T__3);
                                    setState(51);
                                    generic();
                                }
                            }
                            setState(56);
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
                setState(59);
                match(T__4);
                setState(62);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == BOUND) {
                    {
                        setState(60);
                        match(BOUND);
                        setState(61);
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

    public static final String _serializedATN = "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\13C\4\2\t\2\4\3\t" + "\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\6\2\21\n\2\r\2\16\2\22\3\2" + "\3\2\6\2\27\n\2\r\2\16\2\30\5\2\33\n\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\7\4" + "$\n\4\f\4\16\4\'\13\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\5\5\60\n\5\3\6\3\6\3" + "\6\3\6\3\6\7\6\67\n\6\f\6\16\6:\13\6\5\6<\n\6\3\7\3\7\3\7\5\7A\n\7\3\7" + "\2\2\b\2\4\6\b\n\f\2\2\2G\2\32\3\2\2\2\4\34\3\2\2\2\6\36\3\2\2\2\b/\3" + "\2\2\2\n\61\3\2\2\2\f=\3\2\2\2\16\20\5\4\3\2\17\21\7\b\2\2\20\17\3\2\2" + "\2\21\22\3\2\2\2\22\20\3\2\2\2\22\23\3\2\2\2\23\33\3\2\2\2\24\26\5\6\4" + "\2\25\27\7\b\2\2\26\25\3\2\2\2\27\30\3\2\2\2\30\26\3\2\2\2\30\31\3\2\2" + "\2\31\33\3\2\2\2\32\16\3\2\2\2\32\24\3\2\2\2\33\3\3\2\2\2\34\35\7\n\2" + "\2\35\5\3\2\2\2\36\37\5\4\3\2\37 \7\3\2\2 %\5\b\5\2!\"\7\4\2\2\"$\5\b" + "\5\2#!\3\2\2\2$\'\3\2\2\2%#\3\2\2\2%&\3\2\2\2&(\3\2\2\2\'%\3\2\2\2()\7"
            + "\5\2\2)\7\3\2\2\2*\60\5\f\7\2+\60\5\2\2\2,\60\5\4\3\2-\60\5\6\4\2.\60" + "\5\n\6\2/*\3\2\2\2/+\3\2\2\2/,\3\2\2\2/-\3\2\2\2/.\3\2\2\2\60\t\3\2\2" + "\2\61;\7\n\2\2\62\63\7\t\2\2\638\5\6\4\2\64\65\7\6\2\2\65\67\5\6\4\2\66" + "\64\3\2\2\2\67:\3\2\2\28\66\3\2\2\289\3\2\2\29<\3\2\2\2:8\3\2\2\2;\62" + "\3\2\2\2;<\3\2\2\2<\13\3\2\2\2=@\7\7\2\2>?\7\t\2\2?A\5\b\5\2@>\3\2\2\2" + "@A\3\2\2\2A\r\3\2\2\2\n\22\30\32%/8;@";
    public static final ATN _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}