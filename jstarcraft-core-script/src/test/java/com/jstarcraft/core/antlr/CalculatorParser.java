package com.jstarcraft.core.antlr;

import java.util.List;

import org.antlr.v4.runtime.FailedPredicateException;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

@SuppressWarnings({ "all", "warnings", "unchecked", "unused", "cast" })
public class CalculatorParser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache = new PredictionContextCache();
    public static final int Add = 1, Subtract = 2, Multiply = 3, Divide = 4, Number = 5, Open = 6, Close = 7, Space = 8;
    public static final int RULE_formula = 0, RULE_number = 1;

    private static String[] makeRuleNames() {
        return new String[] { "formula", "number" };
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
    public ATN getATN() {
        return _ATN;
    }

    public CalculatorParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    public static class FormulaContext extends ParserRuleContext {
        public TerminalNode Open() {
            return getToken(CalculatorParser.Open, 0);
        }

        public List<FormulaContext> formula() {
            return getRuleContexts(FormulaContext.class);
        }

        public FormulaContext formula(int i) {
            return getRuleContext(FormulaContext.class, i);
        }

        public TerminalNode Close() {
            return getToken(CalculatorParser.Close, 0);
        }

        public NumberContext number() {
            return getRuleContext(NumberContext.class, 0);
        }

        public TerminalNode Multiply() {
            return getToken(CalculatorParser.Multiply, 0);
        }

        public TerminalNode Divide() {
            return getToken(CalculatorParser.Divide, 0);
        }

        public TerminalNode Add() {
            return getToken(CalculatorParser.Add, 0);
        }

        public TerminalNode Subtract() {
            return getToken(CalculatorParser.Subtract, 0);
        }

        public FormulaContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_formula;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CalculatorListener)
                ((CalculatorListener) listener).enterFormula(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CalculatorListener)
                ((CalculatorListener) listener).exitFormula(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CalculatorVisitor)
                return ((CalculatorVisitor<? extends T>) visitor).visitFormula(this);
            else
                return visitor.visitChildren(this);
        }
    }

    public final FormulaContext formula() throws RecognitionException {
        return formula(0);
    }

    private FormulaContext formula(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        FormulaContext _localctx = new FormulaContext(_ctx, _parentState);
        FormulaContext _prevctx = _localctx;
        int _startState = 0;
        enterRecursionRule(_localctx, 0, RULE_formula, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(10);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                case Open: {
                    setState(5);
                    match(Open);
                    setState(6);
                    formula(0);
                    setState(7);
                    match(Close);
                }
                    break;
                case Number: {
                    setState(9);
                    number();
                }
                    break;
                default:
                    throw new NoViableAltException(this);
                }
                _ctx.stop = _input.LT(-1);
                setState(26);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 2, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null)
                            triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            setState(24);
                            _errHandler.sync(this);
                            switch (getInterpreter().adaptivePredict(_input, 1, _ctx)) {
                            case 1: {
                                _localctx = new FormulaContext(_parentctx, _parentState);
                                pushNewRecursionContext(_localctx, _startState, RULE_formula);
                                setState(12);
                                if (!(precpred(_ctx, 5)))
                                    throw new FailedPredicateException(this, "precpred(_ctx, 5)");
                                setState(13);
                                match(Multiply);
                                setState(14);
                                formula(6);
                            }
                                break;
                            case 2: {
                                _localctx = new FormulaContext(_parentctx, _parentState);
                                pushNewRecursionContext(_localctx, _startState, RULE_formula);
                                setState(15);
                                if (!(precpred(_ctx, 4)))
                                    throw new FailedPredicateException(this, "precpred(_ctx, 4)");
                                setState(16);
                                match(Divide);
                                setState(17);
                                formula(5);
                            }
                                break;
                            case 3: {
                                _localctx = new FormulaContext(_parentctx, _parentState);
                                pushNewRecursionContext(_localctx, _startState, RULE_formula);
                                setState(18);
                                if (!(precpred(_ctx, 3)))
                                    throw new FailedPredicateException(this, "precpred(_ctx, 3)");
                                setState(19);
                                match(Add);
                                setState(20);
                                formula(4);
                            }
                                break;
                            case 4: {
                                _localctx = new FormulaContext(_parentctx, _parentState);
                                pushNewRecursionContext(_localctx, _startState, RULE_formula);
                                setState(21);
                                if (!(precpred(_ctx, 2)))
                                    throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                                setState(22);
                                match(Subtract);
                                setState(23);
                                formula(3);
                            }
                                break;
                            }
                        }
                    }
                    setState(28);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 2, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    public static class NumberContext extends ParserRuleContext {
        public TerminalNode Number() {
            return getToken(CalculatorParser.Number, 0);
        }

        public NumberContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_number;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof CalculatorListener)
                ((CalculatorListener) listener).enterNumber(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof CalculatorListener)
                ((CalculatorListener) listener).exitNumber(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof CalculatorVisitor)
                return ((CalculatorVisitor<? extends T>) visitor).visitNumber(this);
            else
                return visitor.visitChildren(this);
        }
    }

    public final NumberContext number() throws RecognitionException {
        NumberContext _localctx = new NumberContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_number);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(29);
                match(Number);
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

    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
        case 0:
            return formula_sempred((FormulaContext) _localctx, predIndex);
        }
        return true;
    }

    private boolean formula_sempred(FormulaContext _localctx, int predIndex) {
        switch (predIndex) {
        case 0:
            return precpred(_ctx, 5);
        case 1:
            return precpred(_ctx, 4);
        case 2:
            return precpred(_ctx, 3);
        case 3:
            return precpred(_ctx, 2);
        }
        return true;
    }

    public static final String _serializedATN = "\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\n\"\4\2\t\2\4\3\t" + "\3\3\2\3\2\3\2\3\2\3\2\3\2\5\2\r\n\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3" + "\2\3\2\3\2\3\2\7\2\33\n\2\f\2\16\2\36\13\2\3\3\3\3\3\3\2\3\2\4\2\4\2\2" + "\2$\2\f\3\2\2\2\4\37\3\2\2\2\6\7\b\2\1\2\7\b\7\b\2\2\b\t\5\2\2\2\t\n\7" + "\t\2\2\n\r\3\2\2\2\13\r\5\4\3\2\f\6\3\2\2\2\f\13\3\2\2\2\r\34\3\2\2\2" + "\16\17\f\7\2\2\17\20\7\5\2\2\20\33\5\2\2\b\21\22\f\6\2\2\22\23\7\6\2\2" + "\23\33\5\2\2\7\24\25\f\5\2\2\25\26\7\3\2\2\26\33\5\2\2\6\27\30\f\4\2\2" + "\30\31\7\4\2\2\31\33\5\2\2\5\32\16\3\2\2\2\32\21\3\2\2\2\32\24\3\2\2\2" + "\32\27\3\2\2\2\33\36\3\2\2\2\34\32\3\2\2\2\34\35\3\2\2\2\35\3\3\2\2\2" + "\36\34\3\2\2\2\37 \7\7\2\2 \5\3\2\2\2\5\f\32\34";
    public static final ATN _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}