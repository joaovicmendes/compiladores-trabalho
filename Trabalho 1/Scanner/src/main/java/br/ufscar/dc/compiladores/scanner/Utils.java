package br.ufscar.dc.compiladores.scanner;

import org.antlr.v4.runtime.Token;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Utils {
    /* Conjunto que representa as palavras-chave e símbolos especiais da linguagem. */
    private static final Set<Integer> languageWords = new HashSet<>() {{
        add(AlgoritmicaScanner.PALAVRA_CHAVE);
        add(AlgoritmicaScanner.TIPO);
        add(AlgoritmicaScanner.DOIS_PONTOS);
        add(AlgoritmicaScanner.INTERVALO);
        add(AlgoritmicaScanner.VIRGULA);
        add(AlgoritmicaScanner.OP_ARITMETICO);
        add(AlgoritmicaScanner.OP_RELACIONAL);
        add(AlgoritmicaScanner.OP_LOGICO);
        add(AlgoritmicaScanner.ATRIBUICAO);
        add(AlgoritmicaScanner.PONTO);
        add(AlgoritmicaScanner.BOOLEANO);
        add(AlgoritmicaScanner.PONTEIRO);
        add(AlgoritmicaScanner.ENDERECO);
        add(AlgoritmicaScanner.ABRE_PARENTESES);
        add(AlgoritmicaScanner.FECHA_PARENTESES);
        add(AlgoritmicaScanner.ABRE_COLCHETE);
        add(AlgoritmicaScanner.FECHA_COLCHETE);
    }};

    /* Conjunto que representa os tokens de erro da linguagem. */
    private static final Map<Integer, String> errorTokens = new HashMap<>() {{
        put(AlgoritmicaScanner.COMENTARIO_NAO_FECHADO, "comentario nao fechado");
        put(AlgoritmicaScanner.CADEIA_NAO_FECHADA, "cadeia literal nao fechada");
        put(AlgoritmicaScanner.CARACTER_INVALIDO, "simbolo nao identificado");
    }};

    /* Função que recebe um Token e retorna sua forma em String apropriada. */
    public static String stringify(Token tk) {
        if (Utils.isError(tk.getType())) {
            return stringifyError(tk);
        }

        StringBuilder tkString = new StringBuilder();
        tkString.append('<');
        tkString.append("'");
        tkString.append(tk.getText());
        tkString.append("'");
        tkString.append(",");

        if (languageWords.contains(tk.getType())) {
            tkString.append("'");
            tkString.append(tk.getText());
            tkString.append("'");
        } else {
            tkString.append(AlgoritmicaScanner.VOCABULARY.getDisplayName(tk.getType()));
        }
        tkString.append(">");

        return tkString.toString();
    }

    /* Função que recebe um Token de erro e o formata de forma especial. */
    public static String stringifyError(Token tk) {
        StringBuilder tkString = new StringBuilder();
        tkString.append("Linha ");
        tkString.append(tk.getLine());
        tkString.append(": ");

        if (tk.getType() == AlgoritmicaScanner.CARACTER_INVALIDO) {
            tkString.append(tk.getText());
            tkString.append(" - ");
        }

        tkString.append(errorTokens.getOrDefault(tk.getType(), ""));

        return tkString.toString();
    }

    /* Função que recebe um tipo de Token e retorna se esse tipo está 
       presente no conjunto de token de erro. */
    public static Boolean isError(int tkType) {
        return errorTokens.containsKey(tkType);
    }
}
