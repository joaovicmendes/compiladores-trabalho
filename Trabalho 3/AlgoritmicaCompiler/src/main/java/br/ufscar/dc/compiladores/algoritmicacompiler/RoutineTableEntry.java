package br.ufscar.dc.compiladores.algoritmicacompiler;

import java.util.List;

class RoutineTableEntry {
    String name;
    SymbolTable.Type returnType;
    List<SymbolTable.Type> paramTypes;
    boolean isProcedure;
    boolean isFunction;

    RoutineTableEntry(String name, SymbolTable.Type returnType, List<SymbolTable.Type> paramTypes) {
        this.name = name;
        this.returnType = returnType;
        this.paramTypes = paramTypes;
        this.isProcedure = returnType == null;
        this.isFunction = !isProcedure;
    }
}
