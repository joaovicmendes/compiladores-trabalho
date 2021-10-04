package br.ufscar.dc.compiladores.algoritmicacompiler;

import br.ufscar.dc.compiladores.parser.AlgoritmicaBaseVisitor;
import br.ufscar.dc.compiladores.parser.AlgoritmicaParser;
import jdk.jshell.execution.Util;

public class CodeGenerationVisitor extends AlgoritmicaBaseVisitor<SymbolTable.Type> {
    Scope scopeStack = new Scope();
    StringBuilder outputCode = new StringBuilder();
    RoutineTable routines = new RoutineTable();

    @Override
    public SymbolTable.Type visitPrograma(AlgoritmicaParser.ProgramaContext ctx) {
        scopeStack.push();

        outputCode.append("#include <stdio.h>\n");
        outputCode.append("#include <stdlib.h>\n");

        visitDeclaracoes(ctx.declaracoes());

        outputCode.append("void main() {\n");

        scopeStack.push();
        visitCorpo(ctx.corpo());
        scopeStack.pop();

        outputCode.append("return 0;\n");
        outputCode.append("}\n");

        scopeStack.pop();
        return null;
    }

    @Override
    public SymbolTable.Type visitDeclaracao_local(AlgoritmicaParser.Declaracao_localContext ctx) {
        if (ctx.isVariable != null) {
            if (ctx.variavel().tipo().registro() != null) {
                scopeStack.push();
            }
            visitVariavel(ctx.variavel());
            if (ctx.variavel().tipo().registro() != null) {
                SymbolTable structTable = scopeStack.pop();
                for (var ident : ctx.variavel().identificador()) {
                    scopeStack.top().add(ident.getText(), SymbolTable.Type.REGISTRO, structTable);
                }
            }
            outputCode.append(";\n");
        }
        else if (ctx.isConstant != null) {
            // const tipo ident[] = valor;
            outputCode.append("const ");
            SymbolTable.Type type = visitTipo_basico(ctx.tipo_basico());

            outputCode.append(ctx.IDENT().getText());
            if (type == SymbolTable.Type.LITERAL) {
                outputCode.append("[80]");
            }
            outputCode.append(" = ");
            visitValor_constante(ctx.valor_constante());
            outputCode.append(";\n");
        }
        else if (ctx.isType != null) {
            outputCode.append("typedef ");
            scopeStack.push();
            visitTipo(ctx.tipo());
            SymbolTable structTable = scopeStack.pop();
            outputCode.append(ctx.IDENT().getText());
            scopeStack.top().add(ctx.IDENT().getText(), SymbolTable.Type.REGISTRO, structTable);
            outputCode.append(";\n");
        }
        return null;
    }

    @Override
    public SymbolTable.Type visitIdentificador(AlgoritmicaParser.IdentificadorContext ctx) {
        outputCode.append(ctx.ident1.getText());

        for (var ident : ctx.outrosIdent) {
            outputCode.append(".");
            outputCode.append(ident.getText());
        }
        visitDimensao(ctx.dimensao());

        for (var scope : scopeStack.toList()) {
            if (scope.contains(ctx.ident1.getText())) {
                SymbolTableEntry ident = scope.get(ctx.ident1.getText());
                if (ident.type != SymbolTable.Type.REGISTRO) {
                    return ident.type;
                } else {
                    SymbolTable.Type type = SymbolTable.Type.REGISTRO;
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
        }
        return null;
    }

    @Override
    public SymbolTable.Type visitVariavel(AlgoritmicaParser.VariavelContext ctx) {
        SymbolTable.Type type = visitTipo(ctx.tipo());

        SymbolTable structTable = null;
        for (var scope : scopeStack.toList()) {
            if (scope.contains(ctx.tipo().getText())){
                structTable = scope.get(ctx.tipo().getText()).childTable;
            }
        }
        scopeStack.top().add(ctx.identificador().get(0).getText(), type, structTable);
        visitIdentificador(ctx.identificador().get(0));
        if (type == SymbolTable.Type.LITERAL) {
            outputCode.append("[80]");
        }

        for (int i = 1; i < ctx.identificador().size(); i++) {
            outputCode.append(",");
            scopeStack.top().add(ctx.identificador().get(i).getText(), type, structTable);
            visitIdentificador(ctx.identificador().get(i));
            if (type == SymbolTable.Type.LITERAL) {
                outputCode.append("[80]");
            }
        }

        return null;
    }

    @Override
    public SymbolTable.Type visitTipo_basico(AlgoritmicaParser.Tipo_basicoContext ctx) {
        if (ctx.literal != null) {
            outputCode.append("char");
            return SymbolTable.Type.LITERAL;
        }
        else if (ctx.inteiro != null) {
            outputCode.append("int");
            return SymbolTable.Type.INTEIRO;
        }
        else if (ctx.real != null) {
            outputCode.append("double");
            return SymbolTable.Type.REAL;
        }
        else if (ctx.logico != null) {
            outputCode.append("bool");
            return SymbolTable.Type.LOGICO;
        }
        return null;
    }

    @Override
    public SymbolTable.Type visitTipo_basico_ident(AlgoritmicaParser.Tipo_basico_identContext ctx) {
        if (ctx.tipo_basico() != null) {
            return visitTipo_basico(ctx.tipo_basico());
        }
        else if (ctx.IDENT() != null) {
            outputCode.append(ctx.IDENT().getText());
            for (var scope : scopeStack.toList()) {
                if (scope.contains(ctx.IDENT().getText())) {
                    return scope.get(ctx.IDENT().getText()).type;
                }
            }
        }
        return null;
    }

    @Override
    public SymbolTable.Type visitTipo_estendido(AlgoritmicaParser.Tipo_estendidoContext ctx) {
        SymbolTable.Type type = visitTipo_basico_ident(ctx.tipo_basico_ident());
        if (ctx.isPointer != null) {
            outputCode.append(" *");
        }

        return type;
    }

    @Override
    public SymbolTable.Type visitExpressao(AlgoritmicaParser.ExpressaoContext ctx) {
        SymbolTable.Type lType = visitTermo_logico(ctx.termo1);
        for (int i = 0; i < ctx.outrosTermos.size(); i++) {
            visitOp_logico_1(ctx.operadores.get(i));

            SymbolTable.Type currType = visitTermo_logico(ctx.outrosTermos.get(i));
            lType = Utils.compatibleType(lType, currType);
        }
        return lType;
    }

    @Override
    public SymbolTable.Type visitOp_logico_1(AlgoritmicaParser.Op_logico_1Context ctx) {
        outputCode.append(" || ");
        return null;
    }

    @Override
    public SymbolTable.Type visitTermo_logico(AlgoritmicaParser.Termo_logicoContext ctx) {
        SymbolTable.Type lType = visitFator_logico(ctx.fator1);
        for (int i = 0; i < ctx.outrosFatores.size(); i++) {
            visitOp_logico_2(ctx.operadores.get(i));

            SymbolTable.Type currType = visitFator_logico(ctx.outrosFatores.get(i));
            lType = Utils.compatibleType(lType, currType);
        }
        return lType;
    }

    @Override
    public SymbolTable.Type visitOp_logico_2(AlgoritmicaParser.Op_logico_2Context ctx) {
        outputCode.append(" && ");
        return null;
    }

    @Override
    public SymbolTable.Type visitFator_logico(AlgoritmicaParser.Fator_logicoContext ctx) {
        if (ctx.not != null) {
            outputCode.append("!");
            visitParcela_logica(ctx.parcela_logica());
            return SymbolTable.Type.LOGICO;
        }
        return visitParcela_logica(ctx.parcela_logica());
    }

    @Override
    public SymbolTable.Type visitParcela_logica(AlgoritmicaParser.Parcela_logicaContext ctx) {
        if (ctx.v != null) {
            outputCode.append("1");
            return SymbolTable.Type.LOGICO;
        }
        else if (ctx.f != null) {
            outputCode.append("0");
            return SymbolTable.Type.LOGICO;
        }
        return visitExp_relacional(ctx.exp_relacional());
    }

    @Override
    public SymbolTable.Type visitExp_relacional(AlgoritmicaParser.Exp_relacionalContext ctx) {
        SymbolTable.Type lType = visitExp_aritmetica(ctx.expressao1);
        for (int i = 0; i < ctx.outrasExpressoes.size(); i++) {
            visitOp_relacional(ctx.operadores.get(i));

            SymbolTable.Type currType = visitExp_aritmetica(ctx.outrasExpressoes.get(i));
            lType = Utils.compatibleType(lType, currType);
        }
        return lType;
    }

    @Override
    public SymbolTable.Type visitOp_relacional(AlgoritmicaParser.Op_relacionalContext ctx) {
        if (ctx.eq != null) {
            outputCode.append(" == ");
        }
        else if (ctx.neq != null) {
            outputCode.append(" != ");
        }
        else if (ctx.geq != null) {
            outputCode.append(" >= ");
        }
        else if (ctx.leq != null) {
            outputCode.append(" <= ");
        }
        else if (ctx.gr != null) {
            outputCode.append(" > ");
        }
        else if (ctx.le != null) {
            outputCode.append(" < ");
        }
        return null;
    }

    @Override
    public SymbolTable.Type visitExp_aritmetica(AlgoritmicaParser.Exp_aritmeticaContext ctx) {
        SymbolTable.Type lType = visitTermo(ctx.termo1);
        for (int i = 0; i < ctx.outrosTermos.size(); i++) {
            visitOp1(ctx.ops.get(i));

            SymbolTable.Type currType = visitTermo(ctx.outrosTermos.get(i));
            lType = Utils.compatibleType(lType, currType);
        }
        return lType;
    }

    @Override
    public SymbolTable.Type visitOp1(AlgoritmicaParser.Op1Context ctx) {
        outputCode.append(" ");
        outputCode.append(ctx.getText());
        outputCode.append(" ");
        return null;
    }

    @Override
    public SymbolTable.Type visitTermo(AlgoritmicaParser.TermoContext ctx) {
        SymbolTable.Type lType = visitFator(ctx.fator1);
        for (int i = 0; i < ctx.outrosFatores.size(); i++) {
            visitOp2(ctx.ops.get(i));

            SymbolTable.Type currType = visitFator(ctx.outrosFatores.get(i));
            lType = Utils.compatibleType(lType, currType);
        }
        return lType;
    }

    @Override
    public SymbolTable.Type visitOp2(AlgoritmicaParser.Op2Context ctx) {
        outputCode.append(" ");
        outputCode.append(ctx.getText());
        outputCode.append(" ");
        return null;
    }

    @Override
    public SymbolTable.Type visitFator(AlgoritmicaParser.FatorContext ctx) {
        SymbolTable.Type lType = visitParcela(ctx.parcela1);
        for (int i = 0; i < ctx.outrasParcelas.size(); i++) {
            visitOp3(ctx.ops.get(i));

            SymbolTable.Type currType = visitParcela(ctx.outrasParcelas.get(i));
            lType = Utils.compatibleType(lType, currType);
        }
        return lType;
    }

    @Override
    public SymbolTable.Type visitOp3(AlgoritmicaParser.Op3Context ctx) {
        outputCode.append(" % ");
        return null;
    }

    @Override
    public SymbolTable.Type visitParcela(AlgoritmicaParser.ParcelaContext ctx) {
        if (ctx.op_unario() != null) {
            outputCode.append("-");
        }

        if (ctx.parcela_unario() != null) {
            return visitParcela_unario(ctx.parcela_unario());
        } else {
            return visitParcela_nao_unario(ctx.parcela_nao_unario());
        }
    }

    @Override
    public SymbolTable.Type visitParcela_unario(AlgoritmicaParser.Parcela_unarioContext ctx) {
        if (ctx.ident != null) {
            if (ctx.isPointer != null) {
                outputCode.append("*");
            }
            return visitIdentificador(ctx.ident);
        }
        else if (ctx.NUM_INT() != null) {
            outputCode.append(ctx.NUM_INT().getText());
            return SymbolTable.Type.INTEIRO;
        }
        else if (ctx.NUM_REAL() != null) {
            outputCode.append(ctx.NUM_REAL().getText());
            return SymbolTable.Type.REAL;
        }
        else if (ctx.identFuncao != null) {
            outputCode.append(ctx.identFuncao.getText());

            outputCode.append("(");
            visitExpressao(ctx.exp1);
            for (var exp : ctx.outrasExp) {
                outputCode.append(",");
                visitExpressao(exp);
            }
            outputCode.append(")");

            for (var scope : scopeStack.toList()) {
                if (scope.contains(ctx.identFuncao.getText())) {
                    return scope.get(ctx.identFuncao.getText()).type;
                }
            }
        }
        return null;
    }

    @Override
    public SymbolTable.Type visitCmdAtribuicao(AlgoritmicaParser.CmdAtribuicaoContext ctx) {
        if (ctx.isPointer != null) {
            outputCode.append("*");
        }

        if (TypeChecker.check(ctx.expressao(), scopeStack) != SymbolTable.Type.LITERAL) {
            visitIdentificador(ctx.identificador());
            outputCode.append("=");
            visitExpressao(ctx.expressao());
        } else {
            outputCode.append("strcpy(");
            visitIdentificador(ctx.identificador());
            outputCode.append(", ");
            visitExpressao(ctx.expressao());
            outputCode.append(")");
        }
        outputCode.append(";\n");
        return null;
    }

    @Override
    public SymbolTable.Type visitCmdSe(AlgoritmicaParser.CmdSeContext ctx) {
        outputCode.append("if (");
        visitExpressao(ctx.expressao());
        outputCode.append(") {\n");

        for (var cmd : ctx.cmd()) {
            visitCmd(cmd);
        }

        outputCode.append("}\n");
        return null;
    }

    @Override
    public SymbolTable.Type visitCmdCaso(AlgoritmicaParser.CmdCasoContext ctx) {
        outputCode.append("switch (");
        visitExp_aritmetica(ctx.exp_aritmetica());
        outputCode.append(") {\n");

        visitSelecao(ctx.selecao());
        if (ctx.def != null) {
            outputCode.append("default:\n");
            for (var cmd : ctx.cmd()) {
                visitCmd(cmd);
            }
        }

        outputCode.append("}\n");
        return null;
    }
}
