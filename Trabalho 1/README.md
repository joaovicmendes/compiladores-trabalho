# Trabalho 1 - Construção de Compiladores (1001497)
O Trabalho 1 da disciplina consiste em implementar um Analisador Léxico para a [Linguagem Algoritmica](https://github.com/joaovicmendes/compiladores-trabalho/blob/main/Trabalho%201/Gram%C3%A1tica%20LA.pdf). O analisador deve receber um arquivo de código fonte e deve retornar uma lista de tokens gerados a partir do código. Mais detalhes na [específicação](https://github.com/joaovicmendes/compiladores-trabalho/blob/main/Trabalho%201/Compiladores.T1.Especifica%C3%A7%C3%A3o.Crit%C3%A9rios.pdf) do projeto.

## Grupo
- [Guilherme Lorençato](https://github.com/GuiLorencato), 758665
- [João Victor Mendes Freire](https://github.com/joaovicmendes), 758943
- [Julia Cinel Chagas](https://github.com/jcinel), 759314

## Como executar

Para executar o código é preciso ter instalado:
- Java 11.0.11
- Maven 3.8.2

Compilar e gerar um arquivo `jar`:
```
mvn clean compile assembly:single
```

Para executar:
```
java -jar target/Scanner-1.0-SNAPSHOT-jar-with-dependencies.jar <entrada> <saida>
```

## Estruturação do código
O trabalho 1 possui três arquivos importantes.
- [`src/main/antlr4/.../AlgoritmicaScanner.g4`](https://github.com/joaovicmendes/compiladores-trabalho/blob/main/Trabalho%201/Scanner/src/main/antlr4/br/ufscar/dc/compiladores/scanner/AlgoritmicaScanner.g4): é o arquivo que contém a descrição dos Tokens da Linguagem Algoritmica. A partir dessas descrições, o ANTLR vai gerar automaticamente um analisador léxico que identifica tais Tokens.
- [`src/main/java/.../scanner/Main.java`](https://github.com/joaovicmendes/compiladores-trabalho/blob/main/Trabalho%201/Scanner/src/main/java/br/ufscar/dc/compiladores/scanner/Main.java): essa classe é o ponto de entrada do programa. É responsável por orquestrar o processo de
  1. Abrir o arquivo de entrada
  2. Ler seus conteúdos, e repassar para o analisador léxico
  3. Escrever os tokens gerados no arquivo de saída.
- [`src/main/java/.../scanner/Utils.java`](https://github.com/joaovicmendes/compiladores-trabalho/blob/main/Trabalho%201/Scanner/src/main/java/br/ufscar/dc/compiladores/scanner/Utils.java): contém alguns utilitários que simplificam os processos de tranformar os Tokens em cadeias de caracteres (para serem escritos nos arquivos) e para identificar Tokens de erros.  
