# Rover

Um simples motor de busca baseado no algoritmo de índice reverso implementado em Java e Spring Boot usando Redis como armazenamento.

Esse projeto expõe uma API HTTP que:
- Registra uma URL e indexa as palavras encontradas no conteúdo de seu HTML;
- Retorna URLs relevantes à busca do usuário;

O processo de indexação consiste em uma sequência de passos:
- Raspagem do conteúdo da URL fornecida;
- Filtragem baseada em palavras de pontuação;
- Tokenização desse conteúdo raspado;
- Lematização desses tokens;

## Dependências

- Spring Web como framework web;
- Spring Data Redis para conexão com Redis;
- Jsoup para coleta de dados de páginas web;
- Stanford Core NLP para tokenização e lematização
- Lombok para redução de boilerplate;


## Endpoints da API
### POST /api/index
Requer uma url no corpo da requisição:

```
{
    "url": "https://www.google.com"
}
```

Comportamento:
1. O servidor requisita a URL;
2. HTML é filtrado, tokenizado e lematizado;
3. Para cada token, uma entrada no Redis é criada:

``
PALAVRA -> SET(url1, url2) 
``

### POST /api/query?query=...
Busca URLs relevantes para a consulta.

Parâmetros da requisição:

``query``: string contendo a consulta.

Comportamento:
1. A consulta é filtrada, tokenizada e lematizada;
2. Para cada token, o Redis é consultado para URLs pertinentes;
3. O resultado é a união entre todos os conjuntos.
