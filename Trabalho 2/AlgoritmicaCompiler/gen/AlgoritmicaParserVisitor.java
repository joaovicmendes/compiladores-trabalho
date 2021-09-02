// Generated from /Users/joaovicmendes/git/compiladores-trabalho/Trabalho 2/Scanner/src/main/antlr4/br/ufscar/dc/compiladores/parser/AlgoritmicaParser.g4 by ANTLR 4.9.1
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link AlgoritmicaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface AlgoritmicaParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link AlgoritmicaParser#programa}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrograma(AlgoritmicaParser.ProgramaContext ctx);
}