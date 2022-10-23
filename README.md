# Desafio
Ler uma planilha do google sheets, buscar as informações necessárias, calcular e escrever o resultado na planilha.

## REGRAS:

Calcular a situação de cada aluno baseado na média das 3 provas (P1, P2 e P3), conforme a tabela:

![alt text](src/main/resources/regra.png)

Caso o número de faltas ultrapasse 25% do número total de aulas o aluno terá a situação "Reprovado por Falta", independente da média.

Caso a situação seja "Exame Final" é necessário calcular a "Nota para Aprovação Final"(naf) de cada aluno de acordo com seguinte fórmula:



5 <= (m + naf)/2



Caso a situação do aluno seja diferente de "Exame Final", preencha o campo "Nota para Aprovação Final" com 0.

Arredondar o resultado para o próximo número inteiro (aumentar) caso necessário.

Utilizar linhas de logs para acompanhamento das atividades da aplicação.

## Como testar aplicaçao
Ao fazer download ou clonar a esta aplicaçao, acesse a pasta "desafio_spreadsheets",
nesta pasta possui dois arquivos com nome gradlew, para executar a aplicaçao basta utilizar um dos comando abaixo no terminal, ou prompt de comandos.

No Linux >: gradlew run

No Windows >: gradlew.bat run

