package br.ufscar.dc.compiladores.algoritmicacompiler;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.PrintWriter;
import java.util.BitSet;

public class AlgoritmicaErrorListener implements ANTLRErrorListener {
    PrintWriter writer;

    public AlgoritmicaErrorListener(PrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
            int charPositionInLine, String msg, RecognitionException e) {
        Token tk = (Token) offendingSymbol;
        if (Utils.isError(tk.getType())) {
            writer.println(Utils.stringifyError(tk));
        } else {
            writer.println(Utils.stringifySyntaxError(line, tk));
        }
        writer.println("Fim da compilacao");
        throw new ParseCancellationException();
    }

    @Override
    public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean b, BitSet bitSet, ATNConfigSet atnConfigSet) {
        // Not being used
    }

    @Override
    public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitSet, ATNConfigSet atnConfigSet) {
        // Not being used
    }

    @Override
    public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atnConfigSet) {
        // Not being used
    }
}
