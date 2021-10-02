package br.ufscar.dc.compiladores.algoritmicacompiler;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    public enum Type {
        INVALIDO,
        LITERAL,
        INTEIRO,
        REAL,
        LOGICO,
        REGISTRO,
    }

    private final Map<String, SymbolTableEntry> table;

    public SymbolTable() {
        this.table = new HashMap<>();
    }

    public void add(String name, SymbolTableEntry entry) {
        table.put(name, entry);
    }

    public void add(String name, SymbolTable.Type type) {
        table.put(name, new SymbolTableEntry(name, type));
    }

    public void add(String name, SymbolTable childTable) {
        table.put(name, new SymbolTableEntry(name, childTable));
    }

    public void add(String name, SymbolTable.Type type, SymbolTable childTable) {
        table.put(name, new SymbolTableEntry(name, type, childTable));
    }

    public boolean contains(String key) {
        return this.table.containsKey(key);
    }

    public SymbolTableEntry get(String key) {
        return this.table.getOrDefault(key, null);
    }

    public void print() {
        table.keySet().forEach(key -> table.get(key).print());
    }
}
