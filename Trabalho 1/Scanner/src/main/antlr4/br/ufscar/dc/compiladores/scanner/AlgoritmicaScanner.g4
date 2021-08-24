lexer grammar AlgoritmicaScanner;

NUM_INT: ('0'..'9')+;

NUM_REAL: ('0'..'9')+ '.' ('0'..'9')+;

PALAVRA_CHAVE: 'algoritmo' 
    | 'fim_algoritmo'
    | 'declare'
    | 'leia'
    | 'escreva'
    | 'se'
    | 'entao'
    | 'senao'
    | 'fim_se'
    | 'faca'
    | 'para'
    | 'fim_para'
    | 'ate'
    | 'enquanto'
    | 'fim_enquanto';

TIPO: 'inteiro'
    | 'real'
    | 'literal';

OP_ARITMETICO: '+'
    | '-'
    | '*'
    | '/'
    | '%';

OP_RELACIONAL: '='
    | '<'
    | '<='
    | '>'
    | '>='
    | '<>';

OP_LOGICO: 'e'
    | 'ou';

ATRIBUICAO: '<-';

PONTEIRO: '^';

ENDERECO: '&';

DOIS_PONTOS: ':';

INTERVALO: '..';

VIRGULA: ',';

ABRE_PARENTESES: '(';

FECHA_PARENTESES: ')';

ABRE_COUCHETE: '[';

FECHA_COUCHETE: ']';

WS: ( ' '
    | '\t'
    | '\r'
    | '\n') {skip();};

IDENT: ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;

CADEIA: ('\'') ('\\\'' | ~('\'' | '\\'))* ('\'')
    | ('"') ('\\"' | ~('"' | '\\'))* ('"');

COMENTARIO: '{' ~('\n' | '\r')* '\r'? '}' {skip();};
