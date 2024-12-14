# MEMBROS DA EQUIPE
Davi Roriz Oliveira<br/>
Estêvão Sousa Vieira<br/>
Joabe Ferreira da Silva Filho<br/>
Larissa de Brito Santos

# DOCUMENTAÇÃO DO PROJETO

1 - Quando o cliente se conecta
Retornar as opções válidas do protocolo
```
{
	"operacoes":{
		"novoInteiroStr":{
			"descricao": "Retorna um número entre 0 e o valor absoluto de max como String"
			"parametros": ["long max"]
		},
		"novoInteiroMax": {
			"descricao": "Retorna um número entre 0 e o valor absoluto de max como long"
			"parametros": ["long max"]
		},
		"novoInteiroMinMax": {
			"descricao": "Retorna um número entre min e o valor absoluto de max como long"
			"parametros": ["long min", "long max"]
		},
		"novoInteiroMinMaxProb": {
			"descricao": "Retorna um número entre 0 e um limite, personalizado com probabilidade"
			"parametros": ["long max1", "long chances1", "long max2", "long chances2"]
		},
		"novoInteiroNL": {
			"descricao": "Retorna um número não nulo, entre 1 e o valor absoluto de max como long"
			"parametros": ["long max"]
		},
		"exit": {
			"descricao": "Encerra a conexão",
			"parametros": []
		}
	}
}
```

2 - Padrão de comunicação
Enviar um dicionário com as informações da requisição
```
{
	"operacao": "valor",
	
	"parametros": {
		"0": 10,
		"1": "string"
	},
	
	"parametros1": 10
	
	"parametros2": "string"
}
```

3 - O cliente seleciona
Envia o função desejada.
```
{
	"operacao": "novoInteiroStr",
	"parametro1": valor,
}
```

4.1 - O servidor retorna Sucesso
Recebe o retorno caso as entradas e parâmetros estejam corretos e imprime o menu novamente.
```
{
	"status": "sucesso",
	"operacao": "novoInteiroStr",
	"resultado": 234
}
```

4.2 - O servidor retorna Error
Recebe o retorno caso as entradas e parâmetros estejam corretas.
```
{
	"status": "error",
	"comando": "novoInteiroNL",
	"details": "mensagem",
}
```

# FUNCIONAMENTO DO SOFTWARE

As chamadas para o servidor são mediadas por um cliente disponibilizado pela implementação, e estão sendo utilizadas na classe AuxLib, do Package Add do programa. 

# PROPÓSITO DO SOFTWARE

O software em questão é um jogo de simulação de vida que roda no terminal. Simples, intuitivo e divertido. Faça escolhas e escreva a sua história.

# MOTIVAÇÃO DA ESCOLHA DO PROTOCOLO
O protocolo escolhido para a realização do projeto foi o protocolo TCP (Transmission Control Protocol), é amplamente utilizado em aplicações de rede devido às suas características de confiabilidade e controle de dados. Como o intuito do projeto tem como objetivo priorizar a confiabilidade, foi preferenciado o protocolo TCP.

# REQUISITOS MÍNIMOS DE FUNCIONAMENTO
Pacote do JSON para java, que pode ser baixadao nesse link: https://github.com/stleary/JSON-java
Pacote do Sockets para Java
