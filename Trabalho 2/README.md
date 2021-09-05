# Trabalho 2 - Construção de Compiladores (1001497)
O Trabalho 2 da disciplina consiste em implementar um Analisador Sintático para a [Linguagem Algoritmica](https://github.com/joaovicmendes/compiladores-trabalho/blob/main/Trabalho%201/Gram%C3%A1tica%20LA.pdf). O analisador deve receber um arquivo de código fonte, utilizar o analisador léxico do trabalho 1 para gerar Tokens e, a partir deles, analisar sintaticamente a linguagem. Mais detalhes na [específicação](https://github.com/joaovicmendes/compiladores-trabalho/blob/main/Trabalho%202/Compiladores.T2.Especifica%C3%A7%C3%A3o.Crit%C3%A9rios.docx.pdf) do projeto.

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
O trabalho 4 possui três arquivos importantes.
- [`src/main/antlr4/.../parser/Algoritmica.g4`](https://github.com/joaovicmendes/compiladores-trabalho/blob/main/Trabalho%202/AlgoritmicaCompiler/src/main/antlr4/br/ufscar/dc/compiladores/parser/Algoritmica.g4): é o arquivo que contém a descrição dos Tokens da Linguagem Algoritmica e das regras Sintáticas do programa. A partir dessas descrições, o ANTLR vai gerar automaticamente um analisador léxico e sintático.
- [`src/main/java/.../algoritmicacompiler/Main.java`](https://github.com/joaovicmendes/compiladores-trabalho/blob/main/Trabalho%202/AlgoritmicaCompiler/src/main/java/br/ufscar/dc/compiladores/algoritmicacompiler/Main.java): essa classe é o ponto de entrada do programa. É responsável por orquestrar o processo de análise do código-fonte lido.
- [`src/main/java/.../algoritmicacompiler/Utils.java`](https://github.com/joaovicmendes/compiladores-trabalho/blob/main/Trabalho%202/AlgoritmicaCompiler/src/main/java/br/ufscar/dc/compiladores/algoritmicacompiler/Utils.java): contém alguns utilitários que simplificam os processos de tranformar os Tokens em cadeias de caracteres (para serem escritos nos arquivos), identificar Tokens de erros e escrever erros sintáticos.
- [`src/main/java/.../algoritmicacompiler/AlgoritmicaErrorListener.java`](https://github.com/joaovicmendes/compiladores-trabalho/blob/main/Trabalho%202/AlgoritmicaCompiler/src/main/java/br/ufscar/dc/compiladores/algoritmicacompiler/AlgoritmicaErrorListener.java): classe que realiza um tratamento de erros customizado.  
