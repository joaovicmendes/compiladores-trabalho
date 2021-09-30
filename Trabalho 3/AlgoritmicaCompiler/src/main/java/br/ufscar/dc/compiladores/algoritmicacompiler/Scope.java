package br.ufscar.dc.compiladores.algoritmicacompiler;

import java.util.LinkedList;
import java.util.List;

public class Scope {
    private final LinkedList<SymbolTable> tableStack;

    public Scope() {
        tableStack = new LinkedList<>();
        push();
    }

    public void push() {
        tableStack.push(new SymbolTable());
    }

    public SymbolTable top() {
        return tableStack.peek();
    }

    public SymbolTable pop() {
        return tableStack.pop();
    }

    public List<SymbolTable> toList() {
        return tableStack;
    }
}
