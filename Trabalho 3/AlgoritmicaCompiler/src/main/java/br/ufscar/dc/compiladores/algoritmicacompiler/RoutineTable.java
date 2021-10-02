package br.ufscar.dc.compiladores.algoritmicacompiler;

import java.util.HashMap;
import java.util.List;

public class RoutineTable {
    private final HashMap<String, RoutineTableEntry> table = new HashMap<>();

    public void add(String name, SymbolTable.Type returnType, List<SymbolTable.Type> paramTypes) {
        table.put(name, new RoutineTableEntry(name, returnType, paramTypes));
    }

    public RoutineTableEntry get(String key) {
        return table.getOrDefault(key, null);
    }

    public boolean contains(String key) {
        return table.containsKey(key);
    }
}
