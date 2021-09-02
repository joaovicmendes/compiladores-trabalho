# Trabalho 2 - Construção de Compiladores (1001497)
O Trabalho 2 da disciplina consiste em implementar um Analisador Sintático para a [Linguagem Algoritmica](https://github.com/joaovicmendes/compiladores-trabalho/blob/main/Trabalho%201/Gram%C3%A1tica%20LA.pdf). O analisador deve receber um arquivo de código fonte, utilizar o analisador léxico do trabalho 1 para gerar Tokens e, a partir deles, analisar sintaticamente a linguagem. Mais detalhes na [específicação]() do projeto.

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
