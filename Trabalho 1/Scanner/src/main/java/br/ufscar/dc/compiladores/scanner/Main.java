package br.ufscar.dc.compiladores.scanner;

import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

public class Main {
    public static void main(String[] args) {
        // Verificação do número de argumentos
        if (args.length < 2) {
            System.out.println("Erro: Número incorreto de parâmetros. Forma correta:");
            System.out.println("    java -jar scanner.jar <input_file> <output_file>");
        }

        // Criando estrutura para ler arquivo contendo código-fonte
        CharStream cs;
        try {
            cs = CharStreams.fromFileName(args[0]);
        } catch (IOException exception) {
            System.out.println("Erro: Não foi possível abrir o arquivo '" + args[0] + "'");
            return;
        }

        // Lista de Tokens gerados
        List<Token> tokens = new ArrayList<>();

        // Analisador Léxico gerado pelo Antlr
        AlgoritmicaScanner scanner = new AlgoritmicaScanner(cs);
        Token currentToken = scanner.nextToken();

        while (currentToken.getType() != Token.EOF) {
            tokens.add(currentToken);
            currentToken = scanner.nextToken();
        }

        // Escrevendo tokens encontrados no arquivo de saída
        try (FileWriter writer = new FileWriter(args[1])) {
            for (Token token : tokens) {
                writer.write(Utils.stringify(token) + System.lineSeparator());
            }
        } catch (IOException exception) {
            System.out.println("Erro: Não foi possível abrir o arquivo '" + args[1] + "'");
        }
    }
}
