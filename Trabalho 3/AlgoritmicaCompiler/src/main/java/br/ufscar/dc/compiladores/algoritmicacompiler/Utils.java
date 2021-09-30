package br.ufscar.dc.compiladores.algoritmicacompiler;

import br.ufscar.dc.compiladores.parser.AlgoritmicaLexer;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    public static List<String> semanticErrors = new ArrayList<>();

    /* Conjunto que representa os tokens de erro da linguagem. */
    private static final Map<Integer, String> errorTokens = new HashMap<>() {{
        put(AlgoritmicaLexer.COMENTARIO_NAO_FECHADO, "comentario nao fechado");
        put(AlgoritmicaLexer.CADEIA_NAO_FECHADA, "cadeia literal nao fechada");
        put(AlgoritmicaLexer.CARACTER_INVALIDO, "simbolo nao identificado");
    }};

    /* Função que recebe um tipo de Token e retorna se esse tipo está
       presente no conjunto de token de erro. */
    public static Boolean isError(int tkType) {
        return errorTokens.containsKey(tkType);
    }

    /* Função que recebe um Token de erro e o formata de forma especial. */
    public static String stringifyError(Token tk) {
        StringBuilder tkString = new StringBuilder();
        tkString.append("Linha ");
        tkString.append(tk.getLine());
        tkString.append(": ");

        if (tk.getType() == AlgoritmicaLexer.CARACTER_INVALIDO) {
            tkString.append(tk.getText());
            tkString.append(" - ");
        }

        tkString.append(errorTokens.getOrDefault(tk.getType(), ""));

        return tkString.toString();
    }

    /* Função que recebe uma linha onde o erro aconteceu e o Token que falhou. Retorna
       a forma em String do erro. */
    public static String stringifySyntaxError(int line, Token tk) {
        StringBuilder tkString = new StringBuilder();
        tkString.append("Linha ");
        tkString.append(line);
        tkString.append(": erro sintatico proximo a ");

        if (tk.getType() == AlgoritmicaLexer.EOF) {
            tkString.append("EOF");
        } else {
            tkString.append(tk.getText());
        }

        return tkString.toString();
    }

    /* Conjunto que representa os tipos da linguagem. */
    private static final Map<String, SymbolTable.Type> typeMap = new HashMap<>() {{
        put("literal", SymbolTable.Type.LITERAL);
        put("inteiro", SymbolTable.Type.INTEIRO);
        put("real", SymbolTable.Type.REAL);
        put("logico", SymbolTable.Type.LOGICO);
    }};

    /* Função que recebe uma String e retorna o tipo equivalente. */
    public static SymbolTable.Type mapStrToType(String type) {
        return typeMap.getOrDefault(type, SymbolTable.Type.INVALIDO);
    }

    /* Função que recebe o token onde um erro aconteceu e uma mensagem e adiciona a
       lista de erros semânticos. */
    public static void addSemanticError(Token tk, String msg) {
        semanticErrors.add(String.format("Linha %d: %s", tk.getLine(), msg));
    }
}
