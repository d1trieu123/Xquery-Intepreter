// Generated from com/cse232/app/XPath.g4 by ANTLR 4.13.1
package com.cse232.app;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class XPathLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		QTEXT=18, WORD=19, AND=20, OR=21, NOT=22, WS=23;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
			"QTEXT", "WORD", "AND", "OR", "NOT", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'/'", "'//'", "'doc('", "')'", "'*'", "'.'", "'..'", "'text()'", 
			"'@attName'", "'('", "'['", "']'", "','", "' = '", "' eq '", "' == '", 
			"' is '", null, null, "' and '", "' or '", "'not '"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, "QTEXT", "WORD", "AND", "OR", "NOT", 
			"WS"
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


	public XPathLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "XPath.g4"; }

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
		"\u0004\u0000\u0017\u008f\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
		"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002"+
		"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002"+
		"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002"+
		"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e"+
		"\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011"+
		"\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014"+
		"\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004"+
		"\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0001\f\u0001"+
		"\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0011\u0001\u0011\u0005\u0011p\b\u0011\n\u0011\f\u0011s\t\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0012\u0004\u0012x\b\u0012\u000b\u0012"+
		"\f\u0012y\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0015\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0000\u0000\u0017\u0001\u0001\u0003"+
		"\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011"+
		"\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u001d\u000f\u001f\u0010"+
		"!\u0011#\u0012%\u0013\'\u0014)\u0015+\u0016-\u0017\u0001\u0000\u0003\u0001"+
		"\u0000\"\"\u0001\u0000AZ\u0003\u0000\t\n\r\r  \u0090\u0000\u0001\u0001"+
		"\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001"+
		"\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000"+
		"\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000"+
		"\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000"+
		"\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000"+
		"\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000"+
		"\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000"+
		"\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001\u0000\u0000\u0000"+
		"\u0000#\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000\u0000\u0000\'"+
		"\u0001\u0000\u0000\u0000\u0000)\u0001\u0000\u0000\u0000\u0000+\u0001\u0000"+
		"\u0000\u0000\u0000-\u0001\u0000\u0000\u0000\u0001/\u0001\u0000\u0000\u0000"+
		"\u00031\u0001\u0000\u0000\u0000\u00054\u0001\u0000\u0000\u0000\u00079"+
		"\u0001\u0000\u0000\u0000\t;\u0001\u0000\u0000\u0000\u000b=\u0001\u0000"+
		"\u0000\u0000\r?\u0001\u0000\u0000\u0000\u000fB\u0001\u0000\u0000\u0000"+
		"\u0011I\u0001\u0000\u0000\u0000\u0013R\u0001\u0000\u0000\u0000\u0015T"+
		"\u0001\u0000\u0000\u0000\u0017V\u0001\u0000\u0000\u0000\u0019X\u0001\u0000"+
		"\u0000\u0000\u001bZ\u0001\u0000\u0000\u0000\u001d^\u0001\u0000\u0000\u0000"+
		"\u001fc\u0001\u0000\u0000\u0000!h\u0001\u0000\u0000\u0000#m\u0001\u0000"+
		"\u0000\u0000%w\u0001\u0000\u0000\u0000\'{\u0001\u0000\u0000\u0000)\u0081"+
		"\u0001\u0000\u0000\u0000+\u0086\u0001\u0000\u0000\u0000-\u008b\u0001\u0000"+
		"\u0000\u0000/0\u0005/\u0000\u00000\u0002\u0001\u0000\u0000\u000012\u0005"+
		"/\u0000\u000023\u0005/\u0000\u00003\u0004\u0001\u0000\u0000\u000045\u0005"+
		"d\u0000\u000056\u0005o\u0000\u000067\u0005c\u0000\u000078\u0005(\u0000"+
		"\u00008\u0006\u0001\u0000\u0000\u00009:\u0005)\u0000\u0000:\b\u0001\u0000"+
		"\u0000\u0000;<\u0005*\u0000\u0000<\n\u0001\u0000\u0000\u0000=>\u0005."+
		"\u0000\u0000>\f\u0001\u0000\u0000\u0000?@\u0005.\u0000\u0000@A\u0005."+
		"\u0000\u0000A\u000e\u0001\u0000\u0000\u0000BC\u0005t\u0000\u0000CD\u0005"+
		"e\u0000\u0000DE\u0005x\u0000\u0000EF\u0005t\u0000\u0000FG\u0005(\u0000"+
		"\u0000GH\u0005)\u0000\u0000H\u0010\u0001\u0000\u0000\u0000IJ\u0005@\u0000"+
		"\u0000JK\u0005a\u0000\u0000KL\u0005t\u0000\u0000LM\u0005t\u0000\u0000"+
		"MN\u0005N\u0000\u0000NO\u0005a\u0000\u0000OP\u0005m\u0000\u0000PQ\u0005"+
		"e\u0000\u0000Q\u0012\u0001\u0000\u0000\u0000RS\u0005(\u0000\u0000S\u0014"+
		"\u0001\u0000\u0000\u0000TU\u0005[\u0000\u0000U\u0016\u0001\u0000\u0000"+
		"\u0000VW\u0005]\u0000\u0000W\u0018\u0001\u0000\u0000\u0000XY\u0005,\u0000"+
		"\u0000Y\u001a\u0001\u0000\u0000\u0000Z[\u0005 \u0000\u0000[\\\u0005=\u0000"+
		"\u0000\\]\u0005 \u0000\u0000]\u001c\u0001\u0000\u0000\u0000^_\u0005 \u0000"+
		"\u0000_`\u0005e\u0000\u0000`a\u0005q\u0000\u0000ab\u0005 \u0000\u0000"+
		"b\u001e\u0001\u0000\u0000\u0000cd\u0005 \u0000\u0000de\u0005=\u0000\u0000"+
		"ef\u0005=\u0000\u0000fg\u0005 \u0000\u0000g \u0001\u0000\u0000\u0000h"+
		"i\u0005 \u0000\u0000ij\u0005i\u0000\u0000jk\u0005s\u0000\u0000kl\u0005"+
		" \u0000\u0000l\"\u0001\u0000\u0000\u0000mq\u0005\"\u0000\u0000np\b\u0000"+
		"\u0000\u0000on\u0001\u0000\u0000\u0000ps\u0001\u0000\u0000\u0000qo\u0001"+
		"\u0000\u0000\u0000qr\u0001\u0000\u0000\u0000rt\u0001\u0000\u0000\u0000"+
		"sq\u0001\u0000\u0000\u0000tu\u0005\"\u0000\u0000u$\u0001\u0000\u0000\u0000"+
		"vx\u0007\u0001\u0000\u0000wv\u0001\u0000\u0000\u0000xy\u0001\u0000\u0000"+
		"\u0000yw\u0001\u0000\u0000\u0000yz\u0001\u0000\u0000\u0000z&\u0001\u0000"+
		"\u0000\u0000{|\u0005 \u0000\u0000|}\u0005a\u0000\u0000}~\u0005n\u0000"+
		"\u0000~\u007f\u0005d\u0000\u0000\u007f\u0080\u0005 \u0000\u0000\u0080"+
		"(\u0001\u0000\u0000\u0000\u0081\u0082\u0005 \u0000\u0000\u0082\u0083\u0005"+
		"o\u0000\u0000\u0083\u0084\u0005r\u0000\u0000\u0084\u0085\u0005 \u0000"+
		"\u0000\u0085*\u0001\u0000\u0000\u0000\u0086\u0087\u0005n\u0000\u0000\u0087"+
		"\u0088\u0005o\u0000\u0000\u0088\u0089\u0005t\u0000\u0000\u0089\u008a\u0005"+
		" \u0000\u0000\u008a,\u0001\u0000\u0000\u0000\u008b\u008c\u0007\u0002\u0000"+
		"\u0000\u008c\u008d\u0001\u0000\u0000\u0000\u008d\u008e\u0006\u0016\u0000"+
		"\u0000\u008e.\u0001\u0000\u0000\u0000\u0003\u0000qy\u0001\u0006\u0000"+
		"\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}