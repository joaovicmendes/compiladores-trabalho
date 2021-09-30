package br.ufscar.dc.compiladores.algoritmicacompiler;

import java.io.IOException;
import java.io.PrintWriter;

import br.ufscar.dc.compiladores.parser.AlgoritmicaParser;
import br.ufscar.dc.compiladores.parser.AlgoritmicaLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public class Main {
    public static void main(String[] args) {
        // Verificação do número de argumentos
        if (args.length < 2) {
            System.out.println("Erro: Número incorreto de parâmetros. Forma correta:");
            System.out.println("    java -jar AlgoritmicaCompiler.jar <input_file> <output_file>");
        }

        // Leitura de arquivo contendo código-fonte
        CharStream cs;
        try {
            cs = CharStreams.fromFileName(args[0]);
        } catch (IOException exception) {
            System.out.println("Erro: Não foi possível abrir o arquivo '" + args[0] + "'");
            return;
        }

        // Criação de analisador léxico e sintático
        AlgoritmicaLexer scanner = new AlgoritmicaLexer(cs);
        CommonTokenStream cts = new CommonTokenStream(scanner);
        AlgoritmicaParser parser = new AlgoritmicaParser(cts);

        try (PrintWriter writer = new PrintWriter(args[1])) {
            // Adicionado tratamento de erros customizado
            AlgoritmicaErrorListener customErrListener = new AlgoritmicaErrorListener(writer);
            parser.removeErrorListeners();
            parser.addErrorListener(customErrListener);

            // Analise sintática
            AlgoritmicaParser.ProgramaContext tree = parser.programa();
            AlgoritmicaVisitor semantic = new AlgoritmicaVisitor();
            semantic.visitPrograma(tree);

            // Imprimindo erros
            Utils.semanticErrors.forEach(writer::println);
            writer.println("Fim da compilacao");
        } catch (IOException exception) {
            System.out.println("Erro: Não foi possível abrir o arquivo '" + args[1] + "'");
        } catch (ParseCancellationException ignored) {}
    }
}
