// $ANTLR 3.2 Sep 23, 2009 12:02:23 C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g 2010-02-14 00:14:41
 package uk.co.dancowan.robots.srv.editors.output; 

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

public class PicoCLexer extends Lexer {
    public static final int EXPONENT=5;
    public static final int OSB=18;
    public static final int KEYWORD=12;
    public static final int SYMBOL=15;
    public static final int CCB=21;
    public static final int UNICODE_ESC=23;
    public static final int OCTAL_ESC=24;
    public static final int CHAR=11;
    public static final int HEX_DIGIT=22;
    public static final int OCB=20;
    public static final int FLOAT=6;
    public static final int INT=4;
    public static final int CSB=19;
    public static final int ID=13;
    public static final int EOF=-1;
    public static final int ORB=16;
    public static final int WS=8;
    public static final int ESC_SEQ=9;
    public static final int OP=14;
    public static final int CRB=17;
    public static final int COMMENT=7;
    public static final int STRING=10;

    // delegates
    // delegators

    public PicoCLexer() {;} 
    public PicoCLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public PicoCLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g"; }

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:6:5: ( ( '0' .. '9' )+ )
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:6:7: ( '0' .. '9' )+
            {
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:6:7: ( '0' .. '9' )+
            int cnt1=0;
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:6:7: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:10:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT )
            int alt8=3;
            alt8 = dfa8.predict(input);
            switch (alt8) {
                case 1 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:10:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )?
                    {
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:10:9: ( '0' .. '9' )+
                    int cnt2=0;
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( ((LA2_0>='0' && LA2_0<='9')) ) {
                            alt2=1;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:10:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt2 >= 1 ) break loop2;
                                EarlyExitException eee =
                                    new EarlyExitException(2, input);
                                throw eee;
                        }
                        cnt2++;
                    } while (true);

                    match('.'); 
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:10:25: ( '0' .. '9' )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0>='0' && LA3_0<='9')) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:10:26: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);

                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:10:37: ( EXPONENT )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0=='E'||LA4_0=='e') ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:10:37: EXPONENT
                            {
                            mEXPONENT(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:11:9: '.' ( '0' .. '9' )+ ( EXPONENT )?
                    {
                    match('.'); 
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:11:13: ( '0' .. '9' )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( ((LA5_0>='0' && LA5_0<='9')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:11:14: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt5 >= 1 ) break loop5;
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
                    } while (true);

                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:11:25: ( EXPONENT )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0=='E'||LA6_0=='e') ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:11:25: EXPONENT
                            {
                            mEXPONENT(); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:12:9: ( '0' .. '9' )+ EXPONENT
                    {
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:12:9: ( '0' .. '9' )+
                    int cnt7=0;
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0>='0' && LA7_0<='9')) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:12:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt7 >= 1 ) break loop7;
                                EarlyExitException eee =
                                    new EarlyExitException(7, input);
                                throw eee;
                        }
                        cnt7++;
                    } while (true);

                    mEXPONENT(); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:16:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' | '/*' ( options {greedy=false; } : . )* '*/' )
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0=='/') ) {
                int LA12_1 = input.LA(2);

                if ( (LA12_1=='/') ) {
                    alt12=1;
                }
                else if ( (LA12_1=='*') ) {
                    alt12=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:16:9: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
                    {
                    match("//"); 

                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:16:14: (~ ( '\\n' | '\\r' ) )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0>='\u0000' && LA9_0<='\t')||(LA9_0>='\u000B' && LA9_0<='\f')||(LA9_0>='\u000E' && LA9_0<='\uFFFF')) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:16:14: ~ ( '\\n' | '\\r' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);

                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:16:28: ( '\\r' )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0=='\r') ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:16:28: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }

                    match('\n'); 
                    _channel=HIDDEN;

                    }
                    break;
                case 2 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:17:9: '/*' ( options {greedy=false; } : . )* '*/'
                    {
                    match("/*"); 

                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:17:14: ( options {greedy=false; } : . )*
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( (LA11_0=='*') ) {
                            int LA11_1 = input.LA(2);

                            if ( (LA11_1=='/') ) {
                                alt11=2;
                            }
                            else if ( ((LA11_1>='\u0000' && LA11_1<='.')||(LA11_1>='0' && LA11_1<='\uFFFF')) ) {
                                alt11=1;
                            }


                        }
                        else if ( ((LA11_0>='\u0000' && LA11_0<=')')||(LA11_0>='+' && LA11_0<='\uFFFF')) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:17:42: .
                    	    {
                    	    matchAny(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop11;
                        }
                    } while (true);

                    match("*/"); 

                    _channel=HIDDEN;

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:20:5: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:20:9: ( ' ' | '\\t' | '\\r' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:28:5: ( '\"' ( ESC_SEQ | ~ ( '\\\\' | '\"' ) )* '\"' )
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:28:8: '\"' ( ESC_SEQ | ~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:28:12: ( ESC_SEQ | ~ ( '\\\\' | '\"' ) )*
            loop13:
            do {
                int alt13=3;
                int LA13_0 = input.LA(1);

                if ( (LA13_0=='\\') ) {
                    alt13=1;
                }
                else if ( ((LA13_0>='\u0000' && LA13_0<='!')||(LA13_0>='#' && LA13_0<='[')||(LA13_0>=']' && LA13_0<='\uFFFF')) ) {
                    alt13=2;
                }


                switch (alt13) {
            	case 1 :
            	    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:28:14: ESC_SEQ
            	    {
            	    mESC_SEQ(); 

            	    }
            	    break;
            	case 2 :
            	    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:28:24: ~ ( '\\\\' | '\"' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "CHAR"
    public final void mCHAR() throws RecognitionException {
        try {
            int _type = CHAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:31:5: ( '\\'' ( ESC_SEQ | ~ ( '\\'' | '\\\\' ) ) '\\'' )
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:31:8: '\\'' ( ESC_SEQ | ~ ( '\\'' | '\\\\' ) ) '\\''
            {
            match('\''); 
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:31:13: ( ESC_SEQ | ~ ( '\\'' | '\\\\' ) )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0=='\\') ) {
                alt14=1;
            }
            else if ( ((LA14_0>='\u0000' && LA14_0<='&')||(LA14_0>='(' && LA14_0<='[')||(LA14_0>=']' && LA14_0<='\uFFFF')) ) {
                alt14=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:31:15: ESC_SEQ
                    {
                    mESC_SEQ(); 

                    }
                    break;
                case 2 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:31:25: ~ ( '\\'' | '\\\\' )
                    {
                    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            match('\''); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CHAR"

    // $ANTLR start "KEYWORD"
    public final void mKEYWORD() throws RecognitionException {
        try {
            int _type = KEYWORD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:35:5: ( 'for' | 'if' | 'else' | 'while' | 'do' | 'return' | 'printf' | 'struct' | 'switch' | 'case\"' | 'char' | 'int' | 'void' | 'unsigned' | 'typedef' | 'short' | '#define' | '#include' | '#ifdef' | '#endif' )
            int alt15=20;
            alt15 = dfa15.predict(input);
            switch (alt15) {
                case 1 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:35:7: 'for'
                    {
                    match("for"); 


                    }
                    break;
                case 2 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:36:7: 'if'
                    {
                    match("if"); 


                    }
                    break;
                case 3 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:37:7: 'else'
                    {
                    match("else"); 


                    }
                    break;
                case 4 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:38:7: 'while'
                    {
                    match("while"); 


                    }
                    break;
                case 5 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:39:7: 'do'
                    {
                    match("do"); 


                    }
                    break;
                case 6 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:40:7: 'return'
                    {
                    match("return"); 


                    }
                    break;
                case 7 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:41:7: 'printf'
                    {
                    match("printf"); 


                    }
                    break;
                case 8 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:42:7: 'struct'
                    {
                    match("struct"); 


                    }
                    break;
                case 9 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:43:7: 'switch'
                    {
                    match("switch"); 


                    }
                    break;
                case 10 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:44:7: 'case\"'
                    {
                    match("case\""); 


                    }
                    break;
                case 11 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:45:7: 'char'
                    {
                    match("char"); 


                    }
                    break;
                case 12 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:46:7: 'int'
                    {
                    match("int"); 


                    }
                    break;
                case 13 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:47:7: 'void'
                    {
                    match("void"); 


                    }
                    break;
                case 14 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:48:7: 'unsigned'
                    {
                    match("unsigned"); 


                    }
                    break;
                case 15 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:49:7: 'typedef'
                    {
                    match("typedef"); 


                    }
                    break;
                case 16 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:50:7: 'short'
                    {
                    match("short"); 


                    }
                    break;
                case 17 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:51:7: '#define'
                    {
                    match("#define"); 


                    }
                    break;
                case 18 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:52:7: '#include'
                    {
                    match("#include"); 


                    }
                    break;
                case 19 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:53:7: '#ifdef'
                    {
                    match("#ifdef"); 


                    }
                    break;
                case 20 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:54:7: '#endif'
                    {
                    match("#endif"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "KEYWORD"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:57:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:57:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:57:31: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( ((LA16_0>='0' && LA16_0<='9')||(LA16_0>='A' && LA16_0<='Z')||LA16_0=='_'||(LA16_0>='a' && LA16_0<='z')) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "OP"
    public final void mOP() throws RecognitionException {
        try {
            int _type = OP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:61:5: ( '+' | '-' | '/' | '*' | '&' | '|' | '=' | '<' | '<<' | '>' | '>>' )
            int alt17=11;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:61:7: '+'
                    {
                    match('+'); 

                    }
                    break;
                case 2 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:62:7: '-'
                    {
                    match('-'); 

                    }
                    break;
                case 3 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:63:7: '/'
                    {
                    match('/'); 

                    }
                    break;
                case 4 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:64:7: '*'
                    {
                    match('*'); 

                    }
                    break;
                case 5 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:65:7: '&'
                    {
                    match('&'); 

                    }
                    break;
                case 6 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:66:7: '|'
                    {
                    match('|'); 

                    }
                    break;
                case 7 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:67:7: '='
                    {
                    match('='); 

                    }
                    break;
                case 8 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:68:7: '<'
                    {
                    match('<'); 

                    }
                    break;
                case 9 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:69:7: '<<'
                    {
                    match("<<"); 


                    }
                    break;
                case 10 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:70:7: '>'
                    {
                    match('>'); 

                    }
                    break;
                case 11 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:71:7: '>>'
                    {
                    match(">>"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OP"

    // $ANTLR start "SYMBOL"
    public final void mSYMBOL() throws RecognitionException {
        try {
            int _type = SYMBOL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:75:5: ( ':' | ';' | ',' | '.' )
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:
            {
            if ( input.LA(1)==','||input.LA(1)=='.'||(input.LA(1)>=':' && input.LA(1)<=';') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SYMBOL"

    // $ANTLR start "ORB"
    public final void mORB() throws RecognitionException {
        try {
            int _type = ORB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:81:5: ( '(' )
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:81:7: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ORB"

    // $ANTLR start "CRB"
    public final void mCRB() throws RecognitionException {
        try {
            int _type = CRB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:82:5: ( ')' )
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:82:7: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CRB"

    // $ANTLR start "OSB"
    public final void mOSB() throws RecognitionException {
        try {
            int _type = OSB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:83:5: ( '[' )
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:83:7: '['
            {
            match('['); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OSB"

    // $ANTLR start "CSB"
    public final void mCSB() throws RecognitionException {
        try {
            int _type = CSB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:84:5: ( ']' )
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:84:7: ']'
            {
            match(']'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CSB"

    // $ANTLR start "OCB"
    public final void mOCB() throws RecognitionException {
        try {
            int _type = OCB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:85:5: ( '{' )
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:85:7: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OCB"

    // $ANTLR start "CCB"
    public final void mCCB() throws RecognitionException {
        try {
            int _type = CCB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:86:5: ( '}' )
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:86:7: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CCB"

    // $ANTLR start "EXPONENT"
    public final void mEXPONENT() throws RecognitionException {
        try {
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:89:10: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:89:12: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:89:22: ( '+' | '-' )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( (LA18_0=='+'||LA18_0=='-') ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:89:33: ( '0' .. '9' )+
            int cnt19=0;
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( ((LA19_0>='0' && LA19_0<='9')) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:89:34: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt19 >= 1 ) break loop19;
                        EarlyExitException eee =
                            new EarlyExitException(19, input);
                        throw eee;
                }
                cnt19++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "EXPONENT"

    // $ANTLR start "HEX_DIGIT"
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:92:11: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:92:13: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "HEX_DIGIT"

    // $ANTLR start "ESC_SEQ"
    public final void mESC_SEQ() throws RecognitionException {
        try {
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:96:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' ) | UNICODE_ESC | OCTAL_ESC )
            int alt20=3;
            int LA20_0 = input.LA(1);

            if ( (LA20_0=='\\') ) {
                switch ( input.LA(2) ) {
                case '\"':
                case '\'':
                case '\\':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                    {
                    alt20=1;
                    }
                    break;
                case 'u':
                    {
                    alt20=2;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    {
                    alt20=3;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:96:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\\\"' | '\\'' | '\\\\' )
                    {
                    match('\\'); 
                    if ( input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||input.LA(1)=='b'||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;
                case 2 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:97:9: UNICODE_ESC
                    {
                    mUNICODE_ESC(); 

                    }
                    break;
                case 3 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:98:9: OCTAL_ESC
                    {
                    mOCTAL_ESC(); 

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "ESC_SEQ"

    // $ANTLR start "OCTAL_ESC"
    public final void mOCTAL_ESC() throws RecognitionException {
        try {
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:103:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt21=3;
            int LA21_0 = input.LA(1);

            if ( (LA21_0=='\\') ) {
                int LA21_1 = input.LA(2);

                if ( ((LA21_1>='0' && LA21_1<='3')) ) {
                    int LA21_2 = input.LA(3);

                    if ( ((LA21_2>='0' && LA21_2<='7')) ) {
                        int LA21_5 = input.LA(4);

                        if ( ((LA21_5>='0' && LA21_5<='7')) ) {
                            alt21=1;
                        }
                        else {
                            alt21=2;}
                    }
                    else {
                        alt21=3;}
                }
                else if ( ((LA21_1>='4' && LA21_1<='7')) ) {
                    int LA21_3 = input.LA(3);

                    if ( ((LA21_3>='0' && LA21_3<='7')) ) {
                        alt21=2;
                    }
                    else {
                        alt21=3;}
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 21, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:103:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:103:14: ( '0' .. '3' )
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:103:15: '0' .. '3'
                    {
                    matchRange('0','3'); 

                    }

                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:103:25: ( '0' .. '7' )
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:103:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:103:36: ( '0' .. '7' )
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:103:37: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 2 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:104:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:104:14: ( '0' .. '7' )
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:104:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:104:25: ( '0' .. '7' )
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:104:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 3 :
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:105:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); 
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:105:14: ( '0' .. '7' )
                    // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:105:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "OCTAL_ESC"

    // $ANTLR start "UNICODE_ESC"
    public final void mUNICODE_ESC() throws RecognitionException {
        try {
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:110:5: ( '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT )
            // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:110:9: '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
            {
            match('\\'); 
            match('u'); 
            mHEX_DIGIT(); 
            mHEX_DIGIT(); 
            mHEX_DIGIT(); 
            mHEX_DIGIT(); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "UNICODE_ESC"

    public void mTokens() throws RecognitionException {
        // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:1:8: ( INT | FLOAT | COMMENT | WS | STRING | CHAR | KEYWORD | ID | OP | SYMBOL | ORB | CRB | OSB | CSB | OCB | CCB )
        int alt22=16;
        alt22 = dfa22.predict(input);
        switch (alt22) {
            case 1 :
                // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:1:10: INT
                {
                mINT(); 

                }
                break;
            case 2 :
                // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:1:14: FLOAT
                {
                mFLOAT(); 

                }
                break;
            case 3 :
                // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:1:20: COMMENT
                {
                mCOMMENT(); 

                }
                break;
            case 4 :
                // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:1:28: WS
                {
                mWS(); 

                }
                break;
            case 5 :
                // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:1:31: STRING
                {
                mSTRING(); 

                }
                break;
            case 6 :
                // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:1:38: CHAR
                {
                mCHAR(); 

                }
                break;
            case 7 :
                // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:1:43: KEYWORD
                {
                mKEYWORD(); 

                }
                break;
            case 8 :
                // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:1:51: ID
                {
                mID(); 

                }
                break;
            case 9 :
                // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:1:54: OP
                {
                mOP(); 

                }
                break;
            case 10 :
                // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:1:57: SYMBOL
                {
                mSYMBOL(); 

                }
                break;
            case 11 :
                // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:1:64: ORB
                {
                mORB(); 

                }
                break;
            case 12 :
                // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:1:68: CRB
                {
                mCRB(); 

                }
                break;
            case 13 :
                // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:1:72: OSB
                {
                mOSB(); 

                }
                break;
            case 14 :
                // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:1:76: CSB
                {
                mCSB(); 

                }
                break;
            case 15 :
                // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:1:80: OCB
                {
                mOCB(); 

                }
                break;
            case 16 :
                // C:\\Documents and Settings\\dan\\workspace\\uk.co.dancowan.robots.srv\\src\\uk\\co\\dancowan\\robots\\srv\\editors\\PicoCLexer.g:1:84: CCB
                {
                mCCB(); 

                }
                break;

        }

    }


    protected DFA8 dfa8 = new DFA8(this);
    protected DFA15 dfa15 = new DFA15(this);
    protected DFA17 dfa17 = new DFA17(this);
    protected DFA22 dfa22 = new DFA22(this);
    static final String DFA8_eotS =
        "\5\uffff";
    static final String DFA8_eofS =
        "\5\uffff";
    static final String DFA8_minS =
        "\2\56\3\uffff";
    static final String DFA8_maxS =
        "\1\71\1\145\3\uffff";
    static final String DFA8_acceptS =
        "\2\uffff\1\2\1\3\1\1";
    static final String DFA8_specialS =
        "\5\uffff}>";
    static final String[] DFA8_transitionS = {
            "\1\2\1\uffff\12\1",
            "\1\4\1\uffff\12\1\13\uffff\1\3\37\uffff\1\3",
            "",
            "",
            ""
    };

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        public String getDescription() {
            return "9:1: FLOAT : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | '.' ( '0' .. '9' )+ ( EXPONENT )? | ( '0' .. '9' )+ EXPONENT );";
        }
    }
    static final String DFA15_eotS =
        "\32\uffff";
    static final String DFA15_eofS =
        "\32\uffff";
    static final String DFA15_minS =
        "\1\43\1\uffff\1\146\5\uffff\1\150\1\141\3\uffff\1\144\10\uffff"+
        "\1\146\3\uffff";
    static final String DFA15_maxS =
        "\1\167\1\uffff\1\156\5\uffff\1\167\1\150\3\uffff\1\151\10\uffff"+
        "\1\156\3\uffff";
    static final String DFA15_acceptS =
        "\1\uffff\1\1\1\uffff\1\3\1\4\1\5\1\6\1\7\2\uffff\1\15\1\16\1\17"+
        "\1\uffff\1\2\1\14\1\10\1\11\1\20\1\12\1\13\1\21\1\uffff\1\24\1\22"+
        "\1\23";
    static final String DFA15_specialS =
        "\32\uffff}>";
    static final String[] DFA15_transitionS = {
            "\1\15\77\uffff\1\11\1\5\1\3\1\1\2\uffff\1\2\6\uffff\1\7\1\uffff"+
            "\1\6\1\10\1\14\1\13\1\12\1\4",
            "",
            "\1\16\7\uffff\1\17",
            "",
            "",
            "",
            "",
            "",
            "\1\22\13\uffff\1\20\2\uffff\1\21",
            "\1\23\6\uffff\1\24",
            "",
            "",
            "",
            "\1\25\1\27\3\uffff\1\26",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\31\7\uffff\1\30",
            "",
            "",
            ""
    };

    static final short[] DFA15_eot = DFA.unpackEncodedString(DFA15_eotS);
    static final short[] DFA15_eof = DFA.unpackEncodedString(DFA15_eofS);
    static final char[] DFA15_min = DFA.unpackEncodedStringToUnsignedChars(DFA15_minS);
    static final char[] DFA15_max = DFA.unpackEncodedStringToUnsignedChars(DFA15_maxS);
    static final short[] DFA15_accept = DFA.unpackEncodedString(DFA15_acceptS);
    static final short[] DFA15_special = DFA.unpackEncodedString(DFA15_specialS);
    static final short[][] DFA15_transition;

    static {
        int numStates = DFA15_transitionS.length;
        DFA15_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA15_transition[i] = DFA.unpackEncodedString(DFA15_transitionS[i]);
        }
    }

    class DFA15 extends DFA {

        public DFA15(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 15;
            this.eot = DFA15_eot;
            this.eof = DFA15_eof;
            this.min = DFA15_min;
            this.max = DFA15_max;
            this.accept = DFA15_accept;
            this.special = DFA15_special;
            this.transition = DFA15_transition;
        }
        public String getDescription() {
            return "34:1: KEYWORD : ( 'for' | 'if' | 'else' | 'while' | 'do' | 'return' | 'printf' | 'struct' | 'switch' | 'case\"' | 'char' | 'int' | 'void' | 'unsigned' | 'typedef' | 'short' | '#define' | '#include' | '#ifdef' | '#endif' );";
        }
    }
    static final String DFA17_eotS =
        "\10\uffff\1\13\1\15\4\uffff";
    static final String DFA17_eofS =
        "\16\uffff";
    static final String DFA17_minS =
        "\1\46\7\uffff\1\74\1\76\4\uffff";
    static final String DFA17_maxS =
        "\1\174\7\uffff\1\74\1\76\4\uffff";
    static final String DFA17_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\2\uffff\1\11\1\10\1\13\1\12";
    static final String DFA17_specialS =
        "\16\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\5\3\uffff\1\4\1\1\1\uffff\1\2\1\uffff\1\3\14\uffff\1\10"+
            "\1\7\1\11\75\uffff\1\6",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\12",
            "\1\14",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;

    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }

    class DFA17 extends DFA {

        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }
        public String getDescription() {
            return "60:1: OP : ( '+' | '-' | '/' | '*' | '&' | '|' | '=' | '<' | '<<' | '>' | '>>' );";
        }
    }
    static final String DFA22_eotS =
        "\1\uffff\1\35\1\26\1\25\3\uffff\14\24\15\uffff\1\24\1\23\3\24\1"+
        "\23\12\24\2\23\14\24\1\23\7\24\2\23\2\24\1\23\4\24\1\23\2\24\4\23"+
        "\3\24\2\23";
    static final String DFA22_eofS =
        "\133\uffff";
    static final String DFA22_minS =
        "\1\11\1\56\1\60\1\52\3\uffff\1\157\1\146\1\154\1\150\1\157\1\145"+
        "\1\162\1\150\1\141\1\157\1\156\1\171\15\uffff\1\162\1\60\1\164\1"+
        "\163\1\151\1\60\1\164\1\151\1\162\1\151\1\157\1\163\1\141\1\151"+
        "\1\163\1\160\2\60\1\145\1\154\1\165\1\156\1\165\1\164\1\162\1\145"+
        "\1\162\1\144\1\151\1\145\1\60\1\145\1\162\1\164\2\143\1\164\1\42"+
        "\2\60\1\147\1\144\1\60\1\156\1\146\1\164\1\150\1\60\1\156\1\145"+
        "\4\60\1\145\1\146\1\144\2\60";
    static final String DFA22_maxS =
        "\1\175\1\145\1\71\1\57\3\uffff\1\157\1\156\1\154\1\150\1\157\1"+
        "\145\1\162\1\167\1\150\1\157\1\156\1\171\15\uffff\1\162\1\172\1"+
        "\164\1\163\1\151\1\172\1\164\1\151\1\162\1\151\1\157\1\163\1\141"+
        "\1\151\1\163\1\160\2\172\1\145\1\154\1\165\1\156\1\165\1\164\1\162"+
        "\1\145\1\162\1\144\1\151\1\145\1\172\1\145\1\162\1\164\2\143\1\164"+
        "\1\42\2\172\1\147\1\144\1\172\1\156\1\146\1\164\1\150\1\172\1\156"+
        "\1\145\4\172\1\145\1\146\1\144\2\172";
    static final String DFA22_acceptS =
        "\4\uffff\1\4\1\5\1\6\14\uffff\1\7\1\10\1\11\1\12\1\13\1\14\1\15"+
        "\1\16\1\17\1\20\1\1\1\2\1\3\73\uffff";
    static final String DFA22_specialS =
        "\133\uffff}>";
    static final String[] DFA22_transitionS = {
            "\2\4\2\uffff\1\4\22\uffff\1\4\1\uffff\1\5\1\23\2\uffff\1\25"+
            "\1\6\1\27\1\30\2\25\1\26\1\25\1\2\1\3\12\1\2\26\3\25\2\uffff"+
            "\32\24\1\31\1\uffff\1\32\1\uffff\1\24\1\uffff\2\24\1\17\1\13"+
            "\1\11\1\7\2\24\1\10\6\24\1\15\1\24\1\14\1\16\1\22\1\21\1\20"+
            "\1\12\3\24\1\33\1\25\1\34",
            "\1\36\1\uffff\12\1\13\uffff\1\36\37\uffff\1\36",
            "\12\36",
            "\1\37\4\uffff\1\37",
            "",
            "",
            "",
            "\1\40",
            "\1\41\7\uffff\1\42",
            "\1\43",
            "\1\44",
            "\1\45",
            "\1\46",
            "\1\47",
            "\1\52\13\uffff\1\50\2\uffff\1\51",
            "\1\53\6\uffff\1\54",
            "\1\55",
            "\1\56",
            "\1\57",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\60",
            "\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\61",
            "\1\62",
            "\1\63",
            "\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\64",
            "\1\65",
            "\1\66",
            "\1\67",
            "\1\70",
            "\1\71",
            "\1\72",
            "\1\73",
            "\1\74",
            "\1\75",
            "\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\76",
            "\1\77",
            "\1\100",
            "\1\101",
            "\1\102",
            "\1\103",
            "\1\104",
            "\1\105",
            "\1\106",
            "\1\107",
            "\1\110",
            "\1\111",
            "\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\112",
            "\1\113",
            "\1\114",
            "\1\115",
            "\1\116",
            "\1\117",
            "\1\23",
            "\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\120",
            "\1\121",
            "\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\122",
            "\1\123",
            "\1\124",
            "\1\125",
            "\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\126",
            "\1\127",
            "\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\1\130",
            "\1\131",
            "\1\132",
            "\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24",
            "\12\24\7\uffff\32\24\4\uffff\1\24\1\uffff\32\24"
    };

    static final short[] DFA22_eot = DFA.unpackEncodedString(DFA22_eotS);
    static final short[] DFA22_eof = DFA.unpackEncodedString(DFA22_eofS);
    static final char[] DFA22_min = DFA.unpackEncodedStringToUnsignedChars(DFA22_minS);
    static final char[] DFA22_max = DFA.unpackEncodedStringToUnsignedChars(DFA22_maxS);
    static final short[] DFA22_accept = DFA.unpackEncodedString(DFA22_acceptS);
    static final short[] DFA22_special = DFA.unpackEncodedString(DFA22_specialS);
    static final short[][] DFA22_transition;

    static {
        int numStates = DFA22_transitionS.length;
        DFA22_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA22_transition[i] = DFA.unpackEncodedString(DFA22_transitionS[i]);
        }
    }

    class DFA22 extends DFA {

        public DFA22(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 22;
            this.eot = DFA22_eot;
            this.eof = DFA22_eof;
            this.min = DFA22_min;
            this.max = DFA22_max;
            this.accept = DFA22_accept;
            this.special = DFA22_special;
            this.transition = DFA22_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( INT | FLOAT | COMMENT | WS | STRING | CHAR | KEYWORD | ID | OP | SYMBOL | ORB | CRB | OSB | CSB | OCB | CCB );";
        }
    }
 

}