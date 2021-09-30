package br.ufscar.dc.compiladores.algoritmicacompiler;

public class SymbolTableEntry {
    String name;
    SymbolTable.Type type;
    SymbolTable childTable;

    public SymbolTableEntry(String name, SymbolTable.Type type) {
        this.name = name;
        this.type = type;
    }

    public SymbolTableEntry(String name, SymbolTable.Type type, SymbolTable childTable) {
        this.name = name;
        this.type = type;
        this.childTable = childTable;
    }

    public SymbolTableEntry(String name, SymbolTable childTable) {
        this.name = name;
        this.childTable = childTable;
    }
}
