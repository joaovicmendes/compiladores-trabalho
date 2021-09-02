// Generated from /Users/joaovicmendes/git/compiladores-trabalho/Trabalho 2/Scanner/src/main/antlr4/br/ufscar/dc/compiladores/parser/AlgoritmicaParser.g4 by ANTLR 4.9.1
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link AlgoritmicaParserVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public class AlgoritmicaParserBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements AlgoritmicaParserVisitor<T> {
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitPrograma(AlgoritmicaParser.ProgramaContext ctx) { return visitChildren(ctx); }
}