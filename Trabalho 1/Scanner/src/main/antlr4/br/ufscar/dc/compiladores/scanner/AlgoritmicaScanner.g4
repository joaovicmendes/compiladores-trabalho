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
    | 'fim_enquanto'
    | 'caso'
    | 'fim_caso'
    | 'seja'
    | 'tipo'
    | 'registro'
    | 'fim_registro'
    | 'procedimento'
    | 'fim_procedimento'
    | 'var'
    | 'retorne'
    | 'funcao'
    | 'fim_funcao'
    | 'constante';

TIPO: 'inteiro'
    | 'real'
    | 'literal'
    | 'logico';

BOOLEANO: 'verdadeiro'
    | 'falso';

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
    | 'ou'
    | 'nao';

ATRIBUICAO: '<-';

PONTEIRO: '^';

ENDERECO: '&';

PONTO: '.';

DOIS_PONTOS: ':';

INTERVALO: '..';

VIRGULA: ',';

ABRE_PARENTESES: '(';

FECHA_PARENTESES: ')';

ABRE_COLCHETE: '[';

FECHA_COLCHETE: ']';

WS: ( ' '
    | '\t'
    | '\r'
    | '\n') -> skip;

IDENT: ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;

CADEIA: '"' ( ~('\n'|'\r') )*? '"';

COMENTARIO: '{' ( ~('\n'|'\r') )*? '}' -> skip;

// Erros léxicos
CADEIA_NAO_FECHADA: ('"') ~('"')*? ('\n'|'\r');

COMENTARIO_NAO_FECHADO: '{' ~('}')*? ('\n'|'\r');

CARACTER_INVALIDO: .+?;
