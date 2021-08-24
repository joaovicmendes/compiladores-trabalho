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
