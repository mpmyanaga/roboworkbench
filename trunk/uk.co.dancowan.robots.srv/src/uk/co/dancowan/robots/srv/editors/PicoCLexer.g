lexer grammar PicoCLexer;

@header { package uk.co.dancowan.robots.srv.editors.output; }


INT :	'0'..'9'+
    ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

COMMENT
    :   '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

STRING
    :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
    ;

CHAR:  '\'' ( ESC_SEQ | ~('\''|'\\') ) '\''
    ;

KEYWORD
    : 'for'
    | 'if'
    | 'else'
    | 'while'
    | 'do'
    | 'return'
    | 'printf'
    | 'struct'
    | 'switch'
    | 'case"'
    | 'char'
    | 'int'
    | 'void'
    | 'unsigned'
    | 'typedef'
    | 'short'
    | '#define'
    | '#include'
    | '#ifdef'
    | '#endif'
    ;
    
ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

OP
    : '+'
    | '-'
    | '/'
    | '*'
    | '&'
    | '|'
    | '='
    | '<'
    | '<<'
    | '>'
    | '>>'
    ;

SYMBOL
    : ':'
    | ';'
    | ','
    | '.'
    ;
	
ORB : '(';
CRB : ')';
OSB : '[';
CSB : ']';
OCB : '{';
CCB : '}';

fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;


