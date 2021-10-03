package br.ufscar.dc.compiladores.algoritmicacompiler;

import br.ufscar.dc.compiladores.parser.AlgoritmicaBaseVisitor;
import br.ufscar.dc.compiladores.parser.AlgoritmicaParser;
import jdk.jshell.execution.Util;
import org.antlr.v4.runtime.Token;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class AlgoritmicaVisitor extends AlgoritmicaBaseVisitor<SymbolTable.Type> {
    Scope scopeStack = new Scope();
    HashSet<String> constants = new HashSet<>();
    RoutineTable routines = new RoutineTable();
    List<SymbolTable.Type> routineParams = new LinkedList<>();
    boolean inRoutine = false;

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
            } else {
                visitVariavel(ctx.variavel());
            }
        }
        // Se estiver declarando uma constante
        else if (ctx.isConstant != null) {
            String ident = ctx.IDENT().getText();
            SymbolTable.Type type = Utils.mapStrToType(ctx.tipo_basico().getText());
            SymbolTable currentScope = scopeStack.top();

            if (currentScope.contains(ident) || constants.contains(ident)) {
                erroIdentificadorDeclarado(ident, ctx.IDENT().getSymbol());
            } else {
                currentScope.add(ident, type);
                constants.add(ident);
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
    public SymbolTable.Type visitDeclaracao_global(AlgoritmicaParser.Declaracao_globalContext ctx) {
        SymbolTable currentScope = scopeStack.top();
        if (ctx.isProcedure != null) {
            String ident = ctx.IDENT().getText();

            if (constants.contains(ident)) {
                erroIdentificadorDeclarado(ident, ctx.IDENT().getSymbol());
            } else {
                currentScope.add(ident, SymbolTable.Type.PROCEDIMENTO);
                constants.add(ident);
            }

            routineParams.clear(); // Esvazia lista de parâmetros globais
            if (ctx.parametros() != null) {
                visitParametros(ctx.parametros());
            }
            routines.add(ident, null, new LinkedList<>(routineParams));

            scopeStack.push();
            for (var decl : ctx.declaracao_local()) {
                visitDeclaracao_local(decl);
            }
            for (var cmd : ctx.cmd()) {
                visitCmd(cmd);
            }
            scopeStack.pop();
            return SymbolTable.Type.PROCEDIMENTO;
        }
        else if (ctx.isFunction != null) {
            SymbolTable.Type returnType = visitTipo_estendido(ctx.tipo_estendido());
            String ident = ctx.IDENT().getText();

            if (constants.contains(ident)) {
                erroIdentificadorDeclarado(ident, ctx.IDENT().getSymbol());
            } else {
                currentScope.add(ident, SymbolTable.Type.FUNCAO);
                constants.add(ident);
            }

            routineParams.clear(); // Esvazia lista de parâmetros globais
            if (ctx.parametros() != null) {
                visitParametros(ctx.parametros());
            }
            routines.add(ident, returnType, new LinkedList<>(routineParams));

            scopeStack.push();
            inRoutine = true;
            for (var decl : ctx.declaracao_local()) {
                visitDeclaracao_local(decl);
            }
            for (var cmd : ctx.cmd()) {
                visitCmd(cmd);
            }
            scopeStack.pop();
            inRoutine = false;
            return SymbolTable.Type.FUNCAO;

        }
        return null;
    }

    @Override
    public SymbolTable.Type visitVariavel(AlgoritmicaParser.VariavelContext ctx) {
        SymbolTable currentScope = scopeStack.top();
        String identName;
        SymbolTable.Type type = visitTipo(ctx.tipo());
        for (var ident : ctx.identificador() ) {
            identName = ident.ident1.getText();
            if (currentScope.contains(identName) || constants.contains(identName)) {
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
        List<SymbolTable> scopes = scopeStack.toList();
        for (var scope : scopes) {
            if (scope.contains(ctx.ident1.getText())) {
                SymbolTableEntry ident = scope.get(ctx.ident1.getText());
                // Se não for um registro, retorna o tipo
                if (ident.type != SymbolTable.Type.REGISTRO) {
                    return ident.type;
                } else {
                    // Se for um registro, retorna o tipo do último campo sendo acessado
                    SymbolTable.Type type = SymbolTable.Type.REGISTRO;

                    if (ctx.pontos.size() > 0) {
                        SymbolTable childTable = ident.childTable;
                        for (var subIdent : ctx.outrosIdent) {
                            if (childTable == null) {
                                return SymbolTable.Type.INVALIDO;
                            }
                            if (!childTable.contains(subIdent.getText())) {
                                erroIdentificadorNaoDeclarado(ctx.getText(), ctx.getStart());
                                return SymbolTable.Type.REGISTRO;
                            } else {
                                type = childTable.get(subIdent.getText()).type;
                                childTable = childTable.get(subIdent.getText()).childTable;
                            }
                        }
                    }
                    return type;
                }
            }
        }
        return null;
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
            SymbolTable.Type identType = visitIdentificador(ident);
            if (identType == null) {
                erroIdentificadorNaoDeclarado(ident.getText(), ident.getStart());
            }
        }

        return null;
    }

    @Override
    public SymbolTable.Type visitParcela_unario(AlgoritmicaParser.Parcela_unarioContext ctx) {
        if (ctx.NUM_INT() != null) {
            return SymbolTable.Type.INTEIRO;
        }
        else if (ctx.NUM_REAL() != null) {
            return SymbolTable.Type.REAL;
        }
        else if (ctx.NUM_REAL() != null) {
            return SymbolTable.Type.REAL;
        }
        else if (ctx.expParentesis != null) {
            return visitExpressao(ctx.expParentesis);
        }
        else if (ctx.ident != null) {
            for (var scope : scopeStack.toList()) {
                if (scope.contains(ctx.ident.ident1.getText())) {
                    SymbolTableEntry ident = scope.get(ctx.ident.ident1.getText());
                    // Se não for um registro, retorna o tipo
                    if (ident.type != SymbolTable.Type.REGISTRO) {
                        return ident.type;
                    } else {
                        // Se for um registro, retorna o tipo do último campo sendo acessado
                        SymbolTable.Type type = SymbolTable.Type.REGISTRO;
                        if (ctx.ident.pontos.size() > 0) {
                            SymbolTable childTable = ident.childTable;
                            for (var subIdent : ctx.ident.outrosIdent) {
                                if (childTable == null) {
                                    return SymbolTable.Type.INVALIDO;
                                }
                                if (!childTable.contains(subIdent.getText())) {
                                    erroIdentificadorNaoDeclarado(ctx.getText(), ctx.getStart());
                                    return SymbolTable.Type.INVALIDO;
                                } else {
                                    type = childTable.get(subIdent.getText()).type;
                                    childTable = childTable.get(subIdent.getText()).childTable;
                                }
                            }
                        }
                        return type;
                    }
                }
            }
            erroIdentificadorNaoDeclarado(ctx.ident.getText(), ctx.ident.getStart());
            return SymbolTable.Type.INVALIDO;
        }
        else /* if (ctx.identFuncao != null) */ {
            if (!routines.contains(ctx.identFuncao.getText())) {
                erroIdentificadorNaoDeclarado(ctx.identFuncao.getText(), ctx.identFuncao);
                return SymbolTable.Type.INVALIDO;
            }
            List<SymbolTable.Type> expectedParams = routines.get(ctx.IDENT().getText()).paramTypes;
            List<SymbolTable.Type> receivedParams = new LinkedList<>();

            receivedParams.add(visitExpressao(ctx.exp1));
            for (var expr : ctx.outrasExp) {
                receivedParams.add(visitExpressao(expr));
            }

            if (expectedParams.size() != receivedParams.size()) {
                erroIncompatibilidadeParametros(ctx.identFuncao.getText(), ctx.identFuncao);
                return SymbolTable.Type.INVALIDO;
            }

            for (int i = 0; i < expectedParams.size(); i++) {
                if (expectedParams.get(i) != receivedParams.get(i)) {
                    Token fault;
                    if (i == 0) {
                        fault = ctx.exp1.getStart();
                    } else {
                        fault = ctx.outrasExp.get(i-1).getStart();
                    }
                    erroIncompatibilidadeParametros(ctx.identFuncao.getText(), fault);
                }
            }

            if (routines.get(ctx.identFuncao.getText()).isFunction) {
                return routines.get(ctx.identFuncao.getText()).returnType;
            }
        }
        return SymbolTable.Type.INVALIDO;
    }

    @Override
    public SymbolTable.Type visitCmdAtribuicao(AlgoritmicaParser.CmdAtribuicaoContext ctx) {
        SymbolTable.Type identType = visitIdentificador(ctx.identificador());
        if (identType == null) {
            erroIdentificadorNaoDeclarado(ctx.identificador().getText(), ctx.identificador().getStart());
        }

        SymbolTable.Type exprType = visitExpressao(ctx.expressao());


        return null;
    }

    @Override
    public SymbolTable.Type visitCmdEscreva(AlgoritmicaParser.CmdEscrevaContext ctx) {
        for (var expr : ctx.expressao()) {
            visitExpressao(expr);
        }

        return null;
    }

    @Override
    public SymbolTable.Type visitExpressao(AlgoritmicaParser.ExpressaoContext ctx) {
        // Obtendo tipo da expressão do lado esquerdo
        SymbolTable.Type lValue = visitTermo_logico(ctx.termo1);
        if (lValue == SymbolTable.Type.INVALIDO) {
            return SymbolTable.Type.INVALIDO;
        }

        // Para cada outro termo, verifica se é compatível
        for (int i = 0; i < ctx.outrosTermos.size(); i++) {
            SymbolTable.Type termType = visitTermo_logico( ctx.outrosTermos.get(i) );
            if (termType == SymbolTable.Type.INVALIDO) {
                return SymbolTable.Type.INVALIDO;
            }

            if (!Utils.isCompatibleType(lValue, termType)) {
                return SymbolTable.Type.INVALIDO;
            }

            lValue = Utils.compatibleType(lValue, termType);
        }

        return lValue;
    }

    @Override
    public SymbolTable.Type visitTermo_logico(AlgoritmicaParser.Termo_logicoContext ctx) {
        // Obtendo tipo do termo do lado esquerdo
        SymbolTable.Type lValue = visitFator_logico(ctx.fator1);
        if (lValue == SymbolTable.Type.INVALIDO) {
            return SymbolTable.Type.INVALIDO;
        }

        // Para cada outro termo, verifica se é compatível
        for (int i = 0; i < ctx.outrosFatores.size(); i++) {
            SymbolTable.Type termType = visitFator_logico( ctx.outrosFatores.get(i) );
            if (termType == SymbolTable.Type.INVALIDO) {
                return SymbolTable.Type.INVALIDO;
            }

            if (!Utils.isCompatibleType(lValue, termType)) {
                return SymbolTable.Type.INVALIDO;
            }

            lValue = Utils.compatibleType(lValue, termType);
        }

        return lValue;
    }

    @Override
    public SymbolTable.Type visitFator_logico(AlgoritmicaParser.Fator_logicoContext ctx) {
        if (ctx.not != null) {
            return SymbolTable.Type.LOGICO;
        } else {
            return visitParcela_logica(ctx.parcela_logica());
        }
    }

    @Override
    public SymbolTable.Type visitParcela_logica(AlgoritmicaParser.Parcela_logicaContext ctx) {
        if (ctx.logica != null) {
            return SymbolTable.Type.LOGICO;
        } else {
            return visitExp_relacional(ctx.exp_relacional());
        }
    }

    @Override
    public SymbolTable.Type visitExp_relacional(AlgoritmicaParser.Exp_relacionalContext ctx) {
        // Obtendo tipo das expressões do lado esquerdo
        SymbolTable.Type lValue = visitExp_aritmetica(ctx.expressao1);
        if (lValue == SymbolTable.Type.INVALIDO) {
            return SymbolTable.Type.INVALIDO;
        }

        // Para cada outra expressão, verifica se é compatível
        for (int i = 0; i < ctx.outrasExpressoes.size(); i++) {
            SymbolTable.Type termType = visitExp_aritmetica( ctx.outrasExpressoes.get(i) );
            if (termType == SymbolTable.Type.INVALIDO) {
                return SymbolTable.Type.INVALIDO;
            }

            if (!Utils.isCompatibleType(lValue, termType)) {
                return SymbolTable.Type.INVALIDO;
            }

            lValue = Utils.compatibleType(lValue, termType);
        }

        return ctx.outrasExpressoes.size() > 0 ? SymbolTable.Type.LOGICO : lValue;
    }

    @Override
    public SymbolTable.Type visitExp_aritmetica(AlgoritmicaParser.Exp_aritmeticaContext ctx) {
        // Obtendo tipo do termo do lado esquerdo
        SymbolTable.Type lValue = visitTermo(ctx.termo1);
        if (lValue == SymbolTable.Type.INVALIDO) {
            return SymbolTable.Type.INVALIDO;
        }

        // Para cada outro termo, verifica se é compatível
        for (int i = 0; i < ctx.outrosTermos.size(); i++) {
            SymbolTable.Type termType = visitTermo( ctx.outrosTermos.get(i) );
            if (termType == SymbolTable.Type.INVALIDO) {
                return SymbolTable.Type.INVALIDO;
            }

            if (!Utils.isCompatibleType(lValue, termType)) {
                return SymbolTable.Type.INVALIDO;
            }

            lValue = Utils.compatibleType(lValue, termType);
        }

        return lValue;
    }

    @Override
    public SymbolTable.Type visitTermo(AlgoritmicaParser.TermoContext ctx) {
        // Obtendo tipo do fator do lado esquerdo
        SymbolTable.Type lValue = visitFator(ctx.fator1);
        if (lValue == SymbolTable.Type.INVALIDO) {
            return SymbolTable.Type.INVALIDO;
        }

        // Para cada outro fator, verifica se é compatível
        for (int i = 0; i < ctx.outrosFatores.size(); i++) {
            SymbolTable.Type termType = visitFator( ctx.outrosFatores.get(i) );
            if (termType == SymbolTable.Type.INVALIDO) {
                return SymbolTable.Type.INVALIDO;
            }

            if (!Utils.isCompatibleType(lValue, termType)) {
                return SymbolTable.Type.INVALIDO;
            }

            lValue = Utils.compatibleType(lValue, termType);
        }

        return lValue;
    }

    @Override
    public SymbolTable.Type visitFator(AlgoritmicaParser.FatorContext ctx) {
        // Obtendo tipo da parcela do lado esquerdo
        SymbolTable.Type lValue = visitParcela(ctx.parcela1);
        if (lValue == SymbolTable.Type.INVALIDO) {
            return SymbolTable.Type.INVALIDO;
        }

        // Para cada outra parcela, verifica se é compatível
        for (int i = 0; i < ctx.outrasParcelas.size(); i++) {
            SymbolTable.Type termType = visitParcela( ctx.outrasParcelas.get(i) );
            if (termType == SymbolTable.Type.INVALIDO) {
                return SymbolTable.Type.INVALIDO;
            }

            if (!Utils.isCompatibleType(lValue, termType)) {
                return SymbolTable.Type.INVALIDO;
            }

            lValue = Utils.compatibleType(lValue, termType);
        }

        return lValue;
    }

    @Override
    public SymbolTable.Type visitParametro(AlgoritmicaParser.ParametroContext ctx) {
        int numberOfParams = ctx.identificador().size();
        SymbolTable.Type paramType = visitTipo_estendido(ctx.tipo_estendido());

        SymbolTable currentScope = scopeStack.top();
        SymbolTable structTable = null;
        if (paramType != SymbolTable.Type.REGISTRO) {
            for (var scope : scopeStack.toList()) {
                if (scope.contains(ctx.tipo_estendido().getText())) {
                    structTable = scope.get(ctx.tipo_estendido().getText()).childTable;
                }
            }
        }
        for (var ident : ctx.identificador()) {
            currentScope.add(ident.getText(), paramType, structTable);
        }
        for (int i = 0; i < numberOfParams; i++) {
            routineParams.add(paramType);
        }

        return paramType;
    }

    @Override
    public SymbolTable.Type visitCmdRetorne(AlgoritmicaParser.CmdRetorneContext ctx) {
        if (!inRoutine) {
            erroRetorneForaDeEscopo(ctx.getStart());
        }
        return null;
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

    public void erroIncompatibilidadeParametros(String ident, Token tk) {
        Utils.addSemanticError(tk, String.format("incompatibilidade de parametros na chamada de %s", ident));
    }

    public void erroRetorneForaDeEscopo(Token tk) {
        Utils.addSemanticError(tk, "comando retorne nao permitido nesse escopo");
    }
}
