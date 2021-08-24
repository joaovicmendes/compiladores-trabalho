package br.ufscar.dc.compiladores.scanner;

import org.antlr.v4.runtime.Token;

import java.util.HashSet;
import java.util.Set;

public class Utils {
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
        add(AlgoritmicaScanner.ABRE_COUCHETE);
        add(AlgoritmicaScanner.FECHA_COUCHETE);
    }};

    public static String stringify(Token tk) {
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
}
