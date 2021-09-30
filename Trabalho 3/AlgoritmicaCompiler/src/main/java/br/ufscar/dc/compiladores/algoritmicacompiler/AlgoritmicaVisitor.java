package br.ufscar.dc.compiladores.algoritmicacompiler;

import br.ufscar.dc.compiladores.parser.AlgoritmicaBaseVisitor;
import br.ufscar.dc.compiladores.parser.AlgoritmicaParser;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class AlgoritmicaVisitor extends AlgoritmicaBaseVisitor<SymbolTable.Type> {
    Scope scopeStack = new Scope();

    @Override
    public SymbolTable.Type visitDeclaracao_local(AlgoritmicaParser.Declaracao_localContext ctx) {
        // Se estiver declarando uma variável
        if (ctx.isVariable != null) {
            // Se estiver declarando um registro
            if (ctx.variavel().tipo().registro() != null) {
                scopeStack.push();
                visitTipo(ctx.variavel().tipo());
                SymbolTable structScope = scopeStack.pop();
                SymbolTable currentScope = scopeStack.top();
                for (var ident : ctx.variavel().identificador()) {
                    currentScope.add(ident.getText(), SymbolTable.Type.REGISTRO, structScope);
                }
            }
            visitVariavel( ctx.variavel() );
        }
        // Se estiver declarando uma constante
        else if (ctx.isConstant != null) {
            String ident = ctx.IDENT().getText();
            SymbolTable.Type type = Utils.mapStrToType(ctx.tipo_basico().getText());
            SymbolTable currentScope = scopeStack.top();

            if (currentScope.contains(ident)) {
                erroIdentificadorDeclarado(ident, ctx.IDENT().getSymbol());
            } else {
                currentScope.add(ident, type);
            }
        }
        // Se estiver declarando um tipo customizado
        else if (ctx.isType != null) {
            String ident = ctx.IDENT().getText();
            SymbolTable currentScope = scopeStack.top();

            if (currentScope.contains(ident)) {
                erroIdentificadorDeclarado(ident, ctx.IDENT().getSymbol());
            } else {
                scopeStack.push(); // Cria um novo escopo para os tipos do registro
                visitTipo( ctx.tipo() );
                SymbolTable typeTable = scopeStack.pop();
                currentScope.add(ident, SymbolTable.Type.REGISTRO, typeTable);
            }
        }
        return null;
    }

    @Override
    public SymbolTable.Type visitVariavel(AlgoritmicaParser.VariavelContext ctx) {
        SymbolTable currentScope = scopeStack.top();
        String identName;
        SymbolTable.Type type = visitTipo(ctx.tipo());
        for (var ident : ctx.identificador() ) {
            identName = ident.getText();
            if (currentScope.contains(identName)) {
                erroIdentificadorDeclarado(identName, ident.getStart());
            } else {
                if (type == SymbolTable.Type.REGISTRO) {
                    List<SymbolTable> scopes = scopeStack.toList();
                    for (var scope : scopes) {
                        if (scope.contains( ctx.tipo().getText() )) {
                            SymbolTable tableCopy = scope.get(ctx.tipo().getText()).childTable;
                            currentScope.add(identName, type, tableCopy);
                        }
                    }
                }
                else {
                    currentScope.add(identName, type);
                }
            }
        }
        return null;
    }

    @Override
    public SymbolTable.Type visitIdentificador(AlgoritmicaParser.IdentificadorContext ctx) {
        SymbolTableEntry idEntry = scopeStack.top().get(ctx.ident1.getText());
        if (idEntry == null) {
            erroIdentificadorNaoDeclarado(ctx.getText(), ctx.getStart());
            return SymbolTable.Type.INVALIDO;
        }

        SymbolTable.Type type = idEntry.type;

        if (ctx.pontos.size() > 0) {
            SymbolTable childTable = idEntry.childTable;
            for (var ident : ctx.outrosIdent) {
                if (!childTable.contains(ident.getText())) {
                    erroIdentificadorNaoDeclarado(ctx.getText(), ctx.getStart());
                    return SymbolTable.Type.INVALIDO;
                } else {
                    type = childTable.get(ident.getText()).type;
                    childTable = childTable.get(ident.getText()).childTable;
                }
            }
        }

        return type;
    }

    @Override
    public SymbolTable.Type visitTipo(AlgoritmicaParser.TipoContext ctx) {
        if (ctx.tipo_estendido() != null) {
            return visitTipo_estendido(ctx.tipo_estendido());
        }
        else if (ctx.registro() != null) {
            return visitRegistro(ctx.registro());
        }
        return null;
    }

    @Override
    public SymbolTable.Type visitTipo_basico_ident(AlgoritmicaParser.Tipo_basico_identContext ctx) {
        if (ctx.tipo_basico() != null) {
            return visitTipo_basico(ctx.tipo_basico());
        }
        else if (ctx.IDENT() != null) {
            List<SymbolTable> scopes = scopeStack.toList();
            for (var scope : scopes) {
                if (scope.contains( ctx.IDENT().getText() )) {
                    return scope.get( ctx.IDENT().getText() ).type;
                }
            }

            erroTipoNaoDeclarado(ctx.IDENT().getText(), ctx.IDENT().getSymbol());
        }
        return SymbolTable.Type.INVALIDO;
    }

    @Override
    public SymbolTable.Type visitTipo_basico(AlgoritmicaParser.Tipo_basicoContext ctx) {
        SymbolTable.Type type = SymbolTable.Type.INVALIDO;

        if (ctx.literal != null)      { type = SymbolTable.Type.LITERAL; }
        else if (ctx.inteiro != null) { type = SymbolTable.Type.INTEIRO; }
        else if (ctx.real != null)    { type = SymbolTable.Type.REAL;    }
        else if (ctx.logico != null)  { type = SymbolTable.Type.LOGICO;  }

        return type;
    }

    @Override
    public SymbolTable.Type visitCorpo(AlgoritmicaParser.CorpoContext ctx) {
        scopeStack.push();
        super.visitCorpo(ctx);
        scopeStack.pop();

        return null;
    }

    @Override
    public SymbolTable.Type visitValor_constante(AlgoritmicaParser.Valor_constanteContext ctx) {
        SymbolTable.Type type = SymbolTable.Type.INVALIDO;

        if (ctx.CADEIA() != null)         { type = SymbolTable.Type.LITERAL; }
        else if (ctx.NUM_INT() != null)   { type = SymbolTable.Type.INTEIRO; }
        else if (ctx.NUM_REAL() != null)  { type = SymbolTable.Type.REAL;    }
        else if (ctx.verdadeiro != null)  { type = SymbolTable.Type.LOGICO;  }
        else if (ctx.falso != null)       { type = SymbolTable.Type.LOGICO;  }

        return type;
    }

    @Override
    public SymbolTable.Type visitCmdLeia(AlgoritmicaParser.CmdLeiaContext ctx) {
        for (var ident : ctx.identificador()) {
            visitIdentificador(ident);
        }

        return null;
    }

    @Override
    public SymbolTable.Type visitParcela_unario(AlgoritmicaParser.Parcela_unarioContext ctx) {
        if (ctx.IDENT() != null) {
            SymbolTableEntry entry = null;
            for (var scope : scopeStack.toList()) {
                if (scope.contains(ctx.IDENT().getText())) {
                    entry = scope.get(ctx.IDENT().getText());
                }
            }

            if (entry == null) {
                erroIdentificadorNaoDeclarado(ctx.IDENT().getText(), ctx.IDENT().getSymbol());
            } else {
                // TODO: checar número de parâmetros e seus tipos
            }
        }
        return super.visitParcela_unario(ctx);
    }

    public void erroIdentificadorDeclarado(String ident, Token tk) {
        Utils.addSemanticError(tk, String.format("identificador %s ja declarado anteriormente", ident));
    }

    public void erroIdentificadorNaoDeclarado(String ident, Token tk) {
        Utils.addSemanticError(tk, String.format("identificador %s nao declarado", ident));
    }

    public void erroTipoNaoDeclarado(String ident, Token tk) {
        Utils.addSemanticError(tk, String.format("tipo %s nao declarado", ident));
    }
}
