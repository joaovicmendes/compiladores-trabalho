# Trabalho 3 - Construção de Compiladores (1001497)
O Trabalho 3 da disciplina consiste em implementar um Analisador Semântico e Gerador de Código C para a [Linguagem Algoritmica](https://github.com/joaovicmendes/compiladores-trabalho/blob/main/Trabalho%201/Gram%C3%A1tica%20LA.pdf). Mais detalhes na [específicação]() do projeto.

## Atenção
O trabalho enviado no dia limite (03/10) é [este aqui](https://github.com/joaovicmendes/compiladores-trabalho/tree/58dea81afc9fd7db41f2a266351b174fbc430f8b). Ele passa em todos os casos do analisador semântico, mas não gera código em C equivalente. O trabalho de fato foi finalizado dia 04/10.

## Grupo
- [Guilherme Lorençato](https://github.com/GuiLorencato), 758665
- [João Victor Mendes Freire](https://github.com/joaovicmendes), 758943
- [Julia Cinel Chagas](https://github.com/jcinel), 759314

## Como executar

Para executar o código é preciso ter instalado:
- Java 11.0.11
- Maven 3.8.2

Abra o projeto em um terminal. Navegue para a pasta `AlgoritmicaCompiler/`.

Para compilar e gerar um arquivo `jar`, execute o comando:
```
mvn clean compile assembly:single
```

Para executar o programa:
```
java -jar target/AlgoritmicaCompiler-1.0-SNAPSHOT-jar-with-dependencies.jar <entrada> <saida>
```

## Estruturação do código

## Estruturação do código
O trabalho 3 possui os seguintes arquivos importantes.
- [`AlgoritmicaVisitor.java`](): visitante responsável por fazer a análise semântica do programa e reportar erros.
- [`CodeGenerationVisitor.java`](): visitante responsável por gerar código C a partir do programa em linguagem Algoritmica.
- [`RoutineTable.java`](): Tabela de Símbolos com informações sobre rotinas (funções e procedimentos).
- [`Scope.java`](): Pilha de escopos do programa.
- [`SymbolTable.java`](): Tabela de Símbolos principal com informações de identificador e tipo.
- [`TypeChecker.java`](): Funções de verificação de tipos de diferentes estruturas.
- [`Utils.java`](https://github.com/joaovicmendes/compiladores-trabalho/blob/main/Trabalho%203/AlgoritmicaCompiler/src/main/java/br/ufscar/dc/compiladores/algoritmicacompiler/Utils.java): contém alguns utilitários que simplificam a obtenção de intervalos, checagem de cadeias e armazenamento dos erros semânticos.
