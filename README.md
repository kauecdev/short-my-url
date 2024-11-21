
# Short My URL

Aplicação Java para encurtamento de URLs.


## Stack utilizada


**Back-end:** Java, AWS Lambda, S3 Storage e AWS API Gateway




## Uso/Exemplos
Para encurtar a URL, faça uma chamada POST para o endpoint:

```
    {url-removed}/create // Serviço desligado para evitar billing
```
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

```
    {url-removed}/{code} // Serviço desligado para evitar billing
```

Passando o código gerado como parâmetro e você será redirecionado para a URL original.



## Aprendizados

Nesse projeto aprendi a utilizar funções lambda na plataforma AWS, além de como armazenar arquivos utilizando o S3 Bucket e criar portas de entrada para funcionalidades com API Gateway. Também aprend a como configurar o permissionamento de serviços com o IAM (Identity Access Management).

## Extra

Como desafio extra, adicionei testes unitários para garantia das funcionalidades utilizando JUnit e Mockito.

![image](https://github.com/user-attachments/assets/736f4ebd-cf29-4804-87f3-6ec4c624f031)


