package br.ufscar.dc.compiladores.algoritmicacompiler;

import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import br.ufscar.dc.compiladores.scanner.AlgoritmicaScanner;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;

public class Main {
    public static void main(String[] args) {
        // Verificação do número de argumentos
        if (args.length < 2) {
            System.out.println("Erro: Número incorreto de parâmetros. Forma correta:");
            System.out.println("    java -jar AlgoritmicaCompiler.jar <input_file> <output_file>");
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
        List<String> tokens = new ArrayList<>();

        // Analisador Léxico gerado pelo Antlr
        AlgoritmicaScanner scanner = new AlgoritmicaScanner(cs);
        Token currentToken = scanner.nextToken();

        while (currentToken.getType() != Token.EOF) {
            tokens.add(Utils.stringify(currentToken));
            if (Utils.isError(currentToken.getType())) {
                break;
            } 
            currentToken = scanner.nextToken();
        }

        // Escrevendo tokens encontrados no arquivo de saída
        try (FileWriter writer = new FileWriter(args[1])) {
            for (String token : tokens) {
                writer.write(token + System.lineSeparator());
            }
        } catch (IOException exception) {
            System.out.println("Erro: Não foi possível abrir o arquivo '" + args[1] + "'");
        }
    }
}
