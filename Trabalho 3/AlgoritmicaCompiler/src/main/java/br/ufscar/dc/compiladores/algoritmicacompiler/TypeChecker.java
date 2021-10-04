package br.ufscar.dc.compiladores.algoritmicacompiler;

import br.ufscar.dc.compiladores.parser.AlgoritmicaParser;

public class TypeChecker {

    public static SymbolTable.Type check(AlgoritmicaParser.ExpressaoContext ctx, Scope scopeStack) {
        SymbolTable.Type lType = check(ctx.termo1, scopeStack);
        for (var termo : ctx.outrosTermos) {
            SymbolTable.Type termoType = check(termo, scopeStack);
            lType = Utils.compatibleType(lType, termoType);
        }
        return  lType;
    }

    public static SymbolTable.Type check(AlgoritmicaParser.Termo_logicoContext ctx, Scope scopeStack) {
        SymbolTable.Type lType = check(ctx.fator1, scopeStack);
        for (var fator : ctx.outrosFatores) {
            SymbolTable.Type fatorType = check(fator, scopeStack);
            lType = Utils.compatibleType(lType, fatorType);
        }
        return  lType;
    }

    public static SymbolTable.Type check(AlgoritmicaParser.Fator_logicoContext ctx, Scope scopeStack) {
        if (ctx.not != null) {
            return SymbolTable.Type.LOGICO;
        }
        return check(ctx.parcela_logica(), scopeStack);
    }

    public static SymbolTable.Type check(AlgoritmicaParser.Parcela_logicaContext ctx, Scope scopeStack) {
        if (ctx.v != null || ctx.f != null) {
            return SymbolTable.Type.LOGICO;
        }
        return check(ctx.exp_relacional(), scopeStack);
    }

    public static SymbolTable.Type check(AlgoritmicaParser.Exp_relacionalContext ctx, Scope scopeStack) {
        SymbolTable.Type lType = check(ctx.expressao1, scopeStack);
        for (var exp : ctx.outrasExpressoes) {
            SymbolTable.Type expType = check(exp, scopeStack);
            lType = Utils.compatibleType(lType, expType);
        }
        return  lType;
    }

    public static SymbolTable.Type check(AlgoritmicaParser.Exp_aritmeticaContext ctx, Scope scopeStack) {
        SymbolTable.Type lType = check(ctx.termo1, scopeStack);
        for (var termo : ctx.outrosTermos) {
            SymbolTable.Type termoType = check(termo, scopeStack);
            lType = Utils.compatibleType(lType, termoType);
        }
        return  lType;
    }

    public static SymbolTable.Type check(AlgoritmicaParser.TermoContext ctx, Scope scopeStack) {
        SymbolTable.Type lType = check(ctx.fator1, scopeStack);
        for (var fator : ctx.outrosFatores) {
            SymbolTable.Type fatorType = check(fator, scopeStack);
            lType = Utils.compatibleType(lType, fatorType);
        }
        return  lType;
    }

    public static SymbolTable.Type check(AlgoritmicaParser.FatorContext ctx, Scope scopeStack) {
        SymbolTable.Type lType = check(ctx.parcela1, scopeStack);
        for (var parcela : ctx.outrasParcelas) {
            SymbolTable.Type parcelaType = check(parcela, scopeStack);
            lType = Utils.compatibleType(lType, parcelaType);
        }
        return  lType;
    }

    public static SymbolTable.Type check(AlgoritmicaParser.ParcelaContext ctx, Scope scopeStack) {
        if (ctx.parcela_unario() != null) {
            return check(ctx.parcela_unario(), scopeStack);
        }
        return check(ctx.parcela_nao_unario(), scopeStack);
    }

    public static SymbolTable.Type check(AlgoritmicaParser.Parcela_unarioContext ctx, Scope scopeStack) {
        if (ctx.ident != null) {
            return check(ctx.ident, scopeStack);
        }
        else if (ctx.identFuncao != null) {
            for (var scope : scopeStack.toList()) {
                if (scope.contains(ctx.identFuncao.getText())) {
                    return scope.get(ctx.identFuncao.getText()).type;
                }
            }
        }
        else if (ctx.NUM_INT() != null) {
            return SymbolTable.Type.INTEIRO;
        }
        else if (ctx.NUM_REAL() != null) {
            return SymbolTable.Type.REAL;
        }
        else if (ctx.expParentesis != null) {
            return check(ctx.expParentesis, scopeStack);
        }
        return null;
    }

    public static SymbolTable.Type check(AlgoritmicaParser.Parcela_nao_unarioContext ctx, Scope scopeStack) {
        if (ctx.identificador() != null) {
            return null;
        } else {
            return SymbolTable.Type.LITERAL;
        }
    }

    public static SymbolTable.Type check(AlgoritmicaParser.IdentificadorContext ctx, Scope scopeStack) {
        for (var scope : scopeStack.toList()) {
            if (scope.contains(ctx.ident1.getText())) {
                SymbolTableEntry ident = scope.get(ctx.ident1.getText());
                SymbolTable.Type type = ident.type;
                if (ctx.pontos.size() > 0) {
                    SymbolTable childTable = ident.childTable;
                    for (var subIdent : ctx.outrosIdent) {
                        if (childTable == null) {
                            return SymbolTable.Type.INVALIDO;
                        }
                        type = childTable.get(subIdent.getText()).type;
                        childTable = childTable.get(subIdent.getText()).childTable;
                    }
                }
                return type;
            }
        }
        return null;
    }

    public static SymbolTable.Type check(AlgoritmicaParser.Tipo_basicoContext ctx, Scope scopeStack) {
        if (ctx.literal != null)      { return SymbolTable.Type.LITERAL; }
        else if (ctx.inteiro != null) { return SymbolTable.Type.INTEIRO; }
        else if (ctx.real != null)    { return SymbolTable.Type.REAL;    }
        else if (ctx.logico != null)  { return SymbolTable.Type.LOGICO;  }
        return null;
    }

    public static SymbolTable.Type check(AlgoritmicaParser.Tipo_basico_identContext ctx, Scope scopeStack) {
        if (ctx.IDENT() != null) {
            for (var scope : scopeStack.toList()) {
                if (scope.contains(ctx.IDENT().getText())) {
                    return scope.get(ctx.IDENT().getText()).type;
                }
            }
        } else {
            return check(ctx.tipo_basico(), scopeStack);
        }
        return null;
    }

    public static SymbolTable.Type check(AlgoritmicaParser.TipoContext ctx, Scope scopeStack) {
        if (ctx.registro() != null)
            return SymbolTable.Type.REGISTRO;
        return check(ctx.tipo_estendido(), scopeStack);
    }

    public static SymbolTable.Type check(AlgoritmicaParser.Tipo_estendidoContext ctx, Scope scopeStack) {
        return check(ctx.tipo_basico_ident(), scopeStack);
    }

    public static SymbolTable.Type check(AlgoritmicaParser.VariavelContext ctx, Scope scopeStack) {
        return check(ctx.tipo(), scopeStack);
    }
}
