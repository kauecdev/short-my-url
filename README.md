
# Short My URL

Aplicação Java para encurtamento de URLs.


## Stack utilizada


**Back-end:** Java, AWS Lambda, S3 Storage e AWS API Gateway




## Uso/Exemplos
Para encurtar a URL, faça uma chamada POST para o endpoint:

`https://qxhpadj6ad.execute-api.us-east-2.amazonaws.com/prod/create`

Com o seguinte body JSON:

```json
{
    "originalUrl": "www.example.com",
    "expirationTime": 17293204 // date in epoch (seconds)
}
```

O resultado dessa chamada retornará um código único:

```json
{
    "code": "185f28d"
}
```

Agora basta chamar o endpoint:

`https://qxhpadj6ad.execute-api.us-east-2.amazonaws.com/prod/{code}`

Passando o código gerado como parâmetro e você será redirecionado para a URL original.



## Aprendizados

Nesse projeto aprendi a utilizar funções lambda na plataforma AWS, além de como armazenar arquivos utilizando o S3 Bucket e criar portas de entrada para funcionalidades com API Gateway. Também aprend a como configurar o permissionamento de serviços com o IAM (Identity Access Management).
