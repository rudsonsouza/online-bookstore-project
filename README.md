# Online Bookstore — Java EE Backend

REST API para gerenciamento de livros, clientes, carrinhos e pedidos. Construído com Java EE 8, WildFly 26, PostgreSQL e Redis, totalmente containerizado com Docker Compose.

---

## Sumário

1. [Pré-requisitos](#pré-requisitos)
2. [Configuração do ambiente](#configuração-do-ambiente)
3. [Build](#build)
4. [Executando o projeto](#executando-o-projeto)
5. [Verificando se está funcionando](#verificando-se-está-funcionando)
6. [API Reference](#api-reference)
7. [Exemplos de requisições](#exemplos-de-requisições)
8. [Testes](#testes)
9. [Arquitetura](#arquitetura)
10. [Estrutura de pastas](#estrutura-de-pastas)
11. [Troubleshooting](#troubleshooting)

---

## Pré-requisitos

Instale as ferramentas abaixo antes de prosseguir:

| Ferramenta | Versão mínima | Verificar |
|---|---|---|
| Java JDK | 8 | `java -version` |
| Maven | 3.6 | `mvn -version` |
| Docker | 20 | `docker -version` |
| Docker Compose | 2 | `docker compose version` |
| curl (opcional) | qualquer | `curl --version` |

---

## Configuração do ambiente

O projeto usa variáveis de ambiente para credenciais. **Nunca edite as credenciais diretamente no código.**

```bash
# 1. Copie o arquivo de exemplo
cp .env.example .env

# 2. Edite .env se quiser senhas diferentes (opcional para desenvolvimento local)
#    Por padrão já está configurado para funcionar com docker-compose
```

O arquivo `.env` nunca é commitado (está no `.gitignore`).

---

## Build

### 1. Baixe o driver JDBC do PostgreSQL

O Dockerfile precisa do driver JDBC para montar o módulo do WildFly:

```bash
curl -L -o docker/wildfly/postgresql-42.6.0.jar \
  https://jdbc.postgresql.org/download/postgresql-42.6.0.jar
```

### 2. Compile o projeto e gere o WAR

```bash
mvn clean package -DskipTests
```

O arquivo gerado será `target/online-bookstore.war`.

---

## Executando o projeto

```bash
docker compose up --build
```

Isso sobe três containers em ordem:

| Container | Porta | Aguarda |
|---|---|---|
| `bookstore-postgres` | 5432 | healthcheck OK |
| `bookstore-redis` | 6379 | healthcheck OK |
| `bookstore-wildfly` | 8080 / 9990 | postgres + redis prontos |

> O WildFly demora cerca de **30–60 segundos** para inicializar. Aguarde a mensagem `WildFly Full 26.1.3.Final (WildFly Core...) started` nos logs antes de fazer requisições.

### Parar os serviços

```bash
# Apenas para os containers (mantém dados)
docker compose down

# Para e remove volumes (apaga banco e cache)
docker compose down -v
```

---

## Verificando se está funcionando

### API de livros (retorna os livros do seed)

```bash
curl http://localhost:8080/online-bookstore/api/books
```

Resposta esperada: array JSON com 5 livros (The Shining, Dune, The Hobbit, etc.)

### OpenAPI — documentação interativa da API

Acesse no navegador:

```
http://localhost:8080/openapi
```

Retorna o schema OpenAPI 3.0 em YAML. Pode ser importado no Postman, Insomnia ou qualquer cliente compatível.

### Console de administração do WildFly (opcional)

```
http://localhost:9990
```

---

## API Reference

**Base URL:** `http://localhost:8080/online-bookstore/api`

Todos os endpoints aceitam e retornam `application/json`. Erros retornam o formato:

```json
{
  "status": 400,
  "message": "Quantity must be at least 1",
  "timestamp": "2026-03-16T10:00:00.000Z"
}
```

### Parâmetros de paginação (endpoints de listagem)

Todos os `GET` de coleção aceitam:

| Parâmetro | Padrão | Descrição |
|---|---|---|
| `page` | `0` | Página (base 0) |
| `size` | `20` | Itens por página |

Exemplo: `GET /api/books?page=1&size=10`

---

### Books — `/api/books`

| Método | Caminho | Descrição |
|---|---|---|
| `POST` | `/api/books` | Cria um livro |
| `GET` | `/api/books` | Lista livros (paginado) |
| `GET` | `/api/books?category=HORROR` | Filtra por categoria |
| `GET` | `/api/books/{id}` | Busca por ID |
| `GET` | `/api/books/isbn/{isbn}` | Busca por ISBN |
| `PUT` | `/api/books/{id}` | Atualiza um livro |
| `DELETE` | `/api/books/{id}` | Remove um livro |

**Categorias disponíveis:** `HORROR`, `SCIENCE_FICTION`, `FANTASY`, `MYSTERY`, `ROMANCE`, `BIOGRAPHY`, `HISTORY`, `SCIENCE`, `TECHNOLOGY`, `OTHER`

**Campos obrigatórios para criação:**

```json
{
  "name": "string",
  "isbn": "string",
  "category": "HORROR",
  "price": 12.99,
  "stockQuantity": 50
}
```

---

### Customers — `/api/customers`

| Método | Caminho | Descrição |
|---|---|---|
| `POST` | `/api/customers` | Cria um cliente |
| `GET` | `/api/customers` | Lista clientes (paginado) |
| `GET` | `/api/customers/{id}` | Busca por ID |
| `GET` | `/api/customers/email/{email}` | Busca por e-mail |
| `PUT` | `/api/customers/{id}` | Atualiza um cliente |
| `DELETE` | `/api/customers/{id}` | Remove um cliente |

**Campos obrigatórios para criação:**

```json
{
  "firstName": "string",
  "lastName": "string",
  "email": "string"
}
```

---

### Cart — `/api/carts`

Carrinhos são armazenados no **Redis** com TTL de 24 horas. Não são persistidos no banco.

| Método | Caminho | Descrição |
|---|---|---|
| `GET` | `/api/carts/{customerId}` | Retorna o carrinho |
| `POST` | `/api/carts/{customerId}/items` | Adiciona item |
| `DELETE` | `/api/carts/{customerId}/items/{bookId}` | Remove item |
| `DELETE` | `/api/carts/{customerId}` | Limpa o carrinho |

**Body para adicionar item:**

```json
{
  "bookId": 1,
  "quantity": 2
}
```

---

### Orders — `/api/orders`

| Método | Caminho | Descrição |
|---|---|---|
| `POST` | `/api/orders` | Cria pedido a partir do carrinho |
| `GET` | `/api/orders` | Lista pedidos (paginado) |
| `GET` | `/api/orders?customerId=1` | Filtra por cliente |
| `GET` | `/api/orders?status=PENDING` | Filtra por status |
| `GET` | `/api/orders/{id}` | Busca por ID |
| `PUT` | `/api/orders/{id}/status` | Atualiza status |

**Body para criar pedido:**

```json
{
  "customerId": 1,
  "deliveryAddress": "Rua das Flores, 100",
  "paymentMethod": "CREDIT_CARD"
}
```

**Status disponíveis:** `PENDING` → `CONFIRMED` → `PROCESSING` → `SHIPPED` → `DELIVERED` (ou `CANCELLED`)

**Métodos de pagamento:** `CREDIT_CARD`, `DEBIT_CARD`, `BANK_TRANSFER`, `PAYPAL`

---

## Exemplos de requisições

O fluxo abaixo usa os dados do **seed** (cliente ID=1, livro ID=1) para criar um pedido completo.

### 1. Ver livros disponíveis

```bash
curl http://localhost:8080/online-bookstore/api/books
```

### 2. Criar um novo livro

```bash
curl -X POST http://localhost:8080/online-bookstore/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "name": "The Great Gatsby",
    "isbn": "978-0743273565",
    "publicationHouse": "Scribner",
    "category": "OTHER",
    "price": 10.99,
    "stockQuantity": 200
  }'
```

### 3. Criar um cliente

```bash
curl -X POST http://localhost:8080/online-bookstore/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Alice",
    "lastName": "Wonder",
    "email": "alice@example.com",
    "mobilePhone": "+55-11-99999-0000",
    "address": "Av. Paulista, 1000, São Paulo"
  }'
```

### 4. Adicionar livro ao carrinho

```bash
curl -X POST http://localhost:8080/online-bookstore/api/carts/1/items \
  -H "Content-Type: application/json" \
  -d '{"bookId": 1, "quantity": 2}'
```

### 5. Ver o carrinho

```bash
curl http://localhost:8080/online-bookstore/api/carts/1
```

### 6. Criar pedido a partir do carrinho

```bash
curl -X POST http://localhost:8080/online-bookstore/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "deliveryAddress": "Av. Paulista, 1000, São Paulo",
    "paymentMethod": "CREDIT_CARD"
  }'
```

### 7. Atualizar status do pedido

```bash
curl -X PUT http://localhost:8080/online-bookstore/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "SHIPPED"}'
```

### 8. Listar pedidos com paginação

```bash
curl "http://localhost:8080/online-bookstore/api/orders?page=0&size=5"
```

---

## Testes

### Rodar todos os testes

```bash
mvn test
```

### Build completo com testes

```bash
mvn clean verify
```

O projeto possui **126 testes unitários** (JUnit 5 + Mockito) cobrindo:
- Entidades (`Book`, `Customer`, `Cart`, `Order`)
- Serviços com mocks (`BookService`, `CustomerService`, `CartService`, `OrderService`)
- Endpoints REST com mocks (`BookResource`, `CustomerResource`, `CartResource`, `OrderResource`)

Os testes **não precisam** de Docker, banco de dados ou Redis para rodar.

---

## Arquitetura

```
┌───────────────────────────────────────────────────────────┐
│                      Docker Compose                       │
│                                                           │
│  ┌─────────────────┐  ┌──────────────┐  ┌─────────────┐  │
│  │    WildFly 26   │  │  PostgreSQL  │  │    Redis    │  │
│  │   (JAX-RS API)  │──│  Port: 5432  │  │ Port: 6379  │  │
│  │   Port: 8080    │  │  (Pedidos,   │  │  (Carrinhos │  │
│  │   Port: 9990    │  │   Livros,    │  │   24h TTL)  │  │
│  │   (management)  │  │   Clientes)  │  │             │  │
│  └─────────────────┘  └──────────────┘  └─────────────┘  │
└───────────────────────────────────────────────────────────┘
```

**Camadas da aplicação:**

```
HTTP Request
    │
    ▼
REST Resource (JAX-RS)        ← validação @Valid, tratamento de erros centralizado
    │
    ▼
Service (EJB/CDI)             ← regras de negócio, cálculo de VAT (BigDecimal)
    │
    ├──▶ Repository (EJB/JPA) ← acesso ao banco, consultas com paginação
    │
    ├──▶ RedisCartStore        ← carrinho serializado como JSON
    │
    └──▶ JMS Producer          ← notificação de estoque zerado (ActiveMQ Artemis)
```

**Stack tecnológica:**

| Componente | Tecnologia |
|---|---|
| REST API | JAX-RS (RESTEasy) |
| Business Logic | EJB Stateless + CDI |
| Persistência | JPA + Hibernate 5.6 |
| Banco de dados | PostgreSQL 15 |
| Migrations | Liquibase 4.20 |
| Cache (carrinho) | Redis 7 + Jedis 4.4 |
| Mensageria | JMS + ActiveMQ Artemis (built-in WildFly) |
| API Docs | MicroProfile OpenAPI (SmallRye) |
| App Server | WildFly 26.1 |
| Build | Maven 3 |
| Testes | JUnit 5 + Mockito 5 |
| CI/CD | GitHub Actions |

---

## Estrutura de pastas

```
online-bookstore-project/
├── .env.example                          # Template de variáveis de ambiente
├── .github/workflows/ci.yml             # Pipeline CI/CD (GitHub Actions)
├── docker-compose.yml                   # Orquestração dos containers
├── Dockerfile                           # Imagem WildFly customizada
├── pom.xml                              # Dependências e build Maven
├── docker/wildfly/
│   ├── configure-wildfly.cli            # Datasource, JMS, Redis no WildFly
│   ├── postgresql-42.6.0.jar            # Driver JDBC (baixar manualmente)
│   └── modules/org/postgresql/main/
│       └── module.xml                   # Módulo WildFly para PostgreSQL
└── src/
    ├── main/java/com/bookstore/
    │   ├── config/
    │   │   └── JaxRsApplication.java    # @ApplicationPath + @OpenAPIDefinition
    │   ├── model/                       # Entidades JPA e POJOs Redis
    │   ├── repository/                  # DAOs com suporte a paginação
    │   ├── service/                     # Lógica de negócio (EJB/CDI)
    │   ├── jms/                         # Produtor/consumidor JMS
    │   └── rest/
    │       ├── GlobalExceptionMapper.java  # Tratamento centralizado de erros
    │       ├── BookResource.java
    │       ├── CustomerResource.java
    │       ├── CartResource.java
    │       ├── OrderResource.java
    │       └── dto/                     # Request/Response DTOs com validações
    └── main/resources/db/changelog/
        ├── 001-create-tables.xml        # Schema inicial
        ├── 002-seed-data.xml            # Dados de exemplo
        └── 003-add-indexes.xml          # Índices de performance
```

---

## Troubleshooting

### `docker compose up` falha com erro de driver JDBC

O arquivo `docker/wildfly/postgresql-42.6.0.jar` não existe. Execute:

```bash
curl -L -o docker/wildfly/postgresql-42.6.0.jar \
  https://jdbc.postgresql.org/download/postgresql-42.6.0.jar
```

---

### WildFly não sobe — erro de `.env`

Certifique-se de que o arquivo `.env` existe na raiz do projeto:

```bash
cp .env.example .env
```

---

### API retorna 404 para todos os endpoints

O WildFly pode ainda não ter terminado o deploy. Aguarde a mensagem nos logs:

```
WildFly Full 26.1.3.Final ... started in ...ms
```

---

### Porta 5432 ou 8080 já em uso

Outro processo está usando a porta. Para verificar:

```bash
# Linux/macOS
lsof -i :8080
lsof -i :5432

# Windows
netstat -ano | findstr :8080
netstat -ano | findstr :5432
```

Pare o processo conflitante ou altere as portas no `docker-compose.yml`.

---

### Erro de variável não definida ao rodar `mvn liquibase:update`

O plugin Liquibase no `pom.xml` usa variáveis de ambiente. Defina-as antes de executar:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/bookstore
export DB_USER=bookstore
export DB_PASSWORD=bookstore

mvn liquibase:update
```

---

### Limpar tudo e começar do zero

```bash
docker compose down -v   # remove containers + volumes
docker compose up --build
```
