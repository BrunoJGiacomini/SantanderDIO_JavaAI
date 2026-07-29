# Budgeting — Santander Bootcamp Java AI

API de gerenciamento de transações financeiras desenvolvida durante o **Santander Bootcamp — Java AI**, utilizando Java, Spring Boot e Spring AI.

O projeto permite cadastrar e consultar transações financeiras e utiliza **Tool Calling** para disponibilizar funcionalidades da aplicação para um assistente de IA.

Além da implementação proposta no projeto-base, foram realizadas melhorias relacionadas a **validação de dados**, **tratamento de valores monetários** e **testes automatizados**.

## Sobre o projeto

O Budgeting é uma API para gerenciamento de gastos financeiros.

A aplicação permite:

* Cadastrar transações financeiras;
* Consultar transações por categoria;
* Gerar um resumo financeiro das transações;
* Utilizar ferramentas (`Tools`) através do Spring AI;
* Processar comandos enviados por áudio;
* Utilizar IA para interpretar solicitações relacionadas às transações;
* Converter respostas da IA em áudio;
* Persistir os dados em um banco MySQL.

A aplicação utiliza o **Spring AI Tool Calling**, permitindo que o modelo de IA utilize funcionalidades reais da aplicação, como cadastrar uma transação ou consultar transações por categoria.

---

## Tecnologias utilizadas

### Backend

* **Java 17**
* **Spring Boot 4.1.0**
* **Spring AI 2.0.0-M4**
* **Spring Web**
* **Spring Data JPA**
* **MySQL**
* **Gradle**
* **Lombok**

### Inteligência Artificial

* **Spring AI**
* **OpenAI**
* **GPT-4o-mini**
* **Whisper** para transcrição de áudio
* **GPT-4o-mini-TTS** para conversão de texto em áudio
* **Tool Calling**

### Testes

* **JUnit 5**
* **AssertJ**
* **Mockito**

A configuração do projeto utiliza Java 17, Spring Boot, Spring AI, Spring Web, Spring Data JPA e o conector do MySQL através do Gradle.

---

## Arquitetura

O projeto possui uma separação entre as principais responsabilidades da aplicação:

```text
src
└── main
    └── java
        └── com.example.budgeting
            ├── application
            │   ├── input
            │   └── output
            │
            ├── domain
            │
            └── infrastructure
                ├── http
                └── persistance
```

A ideia é separar:

* **Domain** → regras e entidades principais do negócio;
* **Application** → casos de uso da aplicação;
* **Infrastructure** → comunicação HTTP, banco de dados e integrações externas.

---

# Integração com IA

Uma das principais características do projeto é a integração com o **Spring AI**.

Os casos de uso podem ser registrados como ferramentas através da anotação:

```java
@Tool
```

Entre as ferramentas disponíveis estão:

### `persist-transaction`

Responsável por cadastrar uma nova transação financeira.

```java
@Tool(
    name = "persist-transaction",
    description = "Persiste uma nova transação financeira"
)
```

### `financial-summary`

Responsável por gerar um resumo das transações cadastradas.

```java
@Tool(
    name = "financial-summary",
    description = "Gera um resumo das transações financeiras cadastradas"
)
```

### Consulta por categoria

A aplicação também disponibiliza uma ferramenta para consultar transações de acordo com sua categoria.

Essas ferramentas são disponibilizadas ao `ChatClient` e podem ser utilizadas pelo modelo durante o processo de Tool Calling.

---

# Tratamento de valores financeiros

Um dos pontos importantes trabalhados durante a evolução do projeto foi o tratamento dos valores monetários.

Internamente, as transações utilizam **centavos** como unidade de armazenamento.

Por exemplo:

```text
10000 → R$ 100,00
25000 → R$ 250,00
5000  → R$ 50,00
```

Para apresentar os valores corretamente na saída da aplicação, foi utilizado `BigDecimal`.

Exemplo:

```java
BigDecimal.valueOf(transaction.getAmount())
        .movePointLeft(2)
        .setScale(2, RoundingMode.HALF_UP);
```

Isso evita tratar valores financeiros simplesmente como operações de ponto flutuante e deixa explícita a conversão de centavos para reais.

O resumo financeiro também utiliza `BigDecimal` para representar:

* valor total;
* valor médio;
* maior gasto.

---

#  Melhorias implementadas

## 1. Validação das transações

A principal melhoria implementada foi a criação de validações antes que uma transação seja persistida.

Antes de chamar o `Repository`, o `PersistTransactionUseCase` verifica:

### Valor

O valor precisa ser maior que zero.

```java
if (input.amount() <= 0) {
    throw new IllegalArgumentException(
        "O valor deve ser maior que zero"
    );
}
```

Isso impede:

```text
R$ 0,00
valores negativos
```

### Descrição

A descrição não pode ser `null` nem vazia.

```java
if (input.description() == null || input.description().isBlank()) {
    throw new IllegalArgumentException(
        "A descrição não pode ser vazia"
    );
}
```

Isso impede entradas como:

```text
""
"   "
null
```

### Categoria

A categoria também precisa ser informada.

```java
if (input.category() == null) {
    throw new IllegalArgumentException(
        "A categoria não pode ser nula"
    );
}
```

Dessa forma, uma transação inválida é rejeitada antes de chegar ao Repository.

---

# Testes automatizados

Foram adicionados testes unitários para garantir o comportamento das validações.

Os principais cenários testados são:

| Cenário           | Resultado esperado     |
| Valor igual a `0` |  Exceção              |
| Valor negativo    |  Exceção              |
| Descrição vazia   |  Exceção              |
| Categoria `null`  |  Exceção              |
| Dados válidos     |  Transação persistida |

Também foi utilizado Mockito para verificar que o Repository **não é chamado quando os dados são inválidos**.

Exemplo:

```java
verify(repository, never()).save(any());
```

Para uma entrada válida, o comportamento esperado é o contrário:

```java
verify(repository).save(any(Transaction.class));
```

Os testes do `PersistTransactionUseCase` cobrem tanto os cenários inválidos quanto o fluxo válido.

---

# Resumo financeiro

A aplicação possui uma ferramenta responsável por gerar um resumo das transações.

O resumo contém:

```text
Quantidade de transações
Valor total
Valor médio
Descrição do maior gasto
Valor do maior gasto
```

Por exemplo, considerando:

```text
Mercado      → R$ 100,00
Combustível  → R$ 250,00
Farmácia     → R$ 50,00
```

O resultado esperado é:

```text
Quantidade: 3
Total: R$ 400,00
Média: R$ 133,33
Maior gasto: Combustível
Valor: R$ 250,00
```

Também existe um teste específico para garantir o arredondamento da média para duas casas decimais.

---

# Endpoints principais

A API utiliza o prefixo:

```text
/transactions
```

## Criar uma transação

```http
POST /transactions
Content-Type: application/json
```

Exemplo:

```json
{
  "description": "Mercado",
  "category": "GROCERIES",
  "amount": 10000
}
```

> O campo `amount` utiliza centavos. Portanto, `10000` representa **R$ 100,00**.

O endpoint chama o caso de uso responsável pela persistência e retorna a transação criada.

---

## Consultar transações por categoria

```http
GET /transactions/{category}
```

Exemplo:

```http
GET /transactions/GROCERIES
```

O endpoint retorna as transações pertencentes à categoria informada.

---

## Endpoint de IA

A aplicação também possui um endpoint para processamento de áudio:

```http
POST /transactions/ai
Content-Type: multipart/form-data
```

O arquivo de áudio é transcrito utilizando o modelo de transcrição da OpenAI.

Depois:

```text
Áudio
   ↓
Transcrição
   ↓
ChatClient
   ↓
Spring AI
   ↓
Tool Calling
   ↓
Resultado
   ↓
Text-to-Speech
   ↓
Áudio MP3
```

O controlador utiliza o `TranscriptionModel`, `ChatClient` e `TextToSpeechModel` para realizar esse fluxo.

---

# Banco de dados

O projeto utiliza **MySQL**.

Existe um `compose.yml` configurado para subir o banco através do Docker Compose.

Configuração utilizada:

```text
Database: transaction
User: app
Password: app
Porta externa: 3307
Porta interna MySQL: 3306
```

O arquivo também configura um volume para manter os dados do banco.

Para iniciar o banco:

```bash
docker compose up -d
```

---

# Como executar o projeto

## 1. Pré-requisitos

Antes de executar a aplicação, é necessário ter instalado:

* Java 17 ou superior;
* Docker;
* Docker Compose;
* Git.

---

## 2. Clone o projeto

```bash
git clone https://github.com/BrunoJGiacomini/SantanderDIO_JavaAI.git
```

Entre na pasta:

```bash
cd SantanderDIO_JavaAI
```

---

## 3. Configure a chave da OpenAI

A aplicação utiliza a variável de ambiente:

```text
OPENAI_API_KEY
```

A configuração do projeto referencia essa variável para autenticar as chamadas da OpenAI.

Crie um arquivo `.env` na raiz do projeto:

```env
OPENAI_API_KEY=sua_chave_aqui
```

**Não publique sua chave da OpenAI no GitHub.**

O projeto já possui configuração para carregar variáveis do arquivo `.env` durante a execução pelo Gradle.

---

## 4. Inicie o banco

```bash
docker compose up -d
```

---

## 5. Execute a aplicação

No Windows:

```bash
gradlew.bat bootRun
```

Linux/macOS:

```bash
./gradlew bootRun
```

---

# Como executar os testes

Para executar os testes:

```bash
./gradlew test
```

No Windows:

```bash
gradlew.bat test
```

Também é possível executar testes específicos.

Por exemplo:

```bash
./gradlew test --tests "*FinancialSummaryUseCaseTest"
```

ou:

```bash
./gradlew test --tests "*PersistTransactionUseCaseTest"
```

Os testes unitários utilizam JUnit, AssertJ e Mockito.

> Alguns testes de integração dependem da configuração de serviços externos utilizados pela aplicação, como a integração com a OpenAI. Por isso, a execução completa da suíte pode depender da disponibilidade da API e da configuração correta das credenciais.

---

# Exemplo do fluxo principal

Um exemplo simplificado do fluxo de criação de uma transação:

```text
Cliente
   │
   │ POST /transactions
   ▼
TransactionController
   │
   ▼
PersistTransactionUseCase
   │
   ├── Valida valor
   ├── Valida descrição
   ├── Valida categoria
   │
   ▼
Transaction
   │
   ▼
TransactionRepository
   │
   ▼
MySQL
```

Quando a entrada é inválida:

```text
Cliente
   │
   ▼
PersistTransactionUseCase
   │
   ├──  Valor inválido
   │
   └── IllegalArgumentException
```

Nesse cenário, o Repository não é chamado.

---

# O que aprendi durante o desafio

Durante o desenvolvimento deste projeto, os principais aprendizados foram:

### Spring Boot

Aprendi melhor como estruturar uma aplicação backend utilizando Spring Boot, incluindo Controllers, Services e injeção de dependências.

### Spring AI

Entendi como uma aplicação Java pode integrar modelos de IA utilizando o Spring AI e como funcionalidades da própria aplicação podem ser disponibilizadas para o modelo através de **Tool Calling**.

### Tool Calling

Um dos principais conceitos trabalhados foi entender que a IA não precisa executar diretamente toda a lógica da aplicação.

Ela pode identificar qual ferramenta precisa utilizar e chamar um método existente no backend.

Por exemplo:

```text
Usuário:
"Registre um gasto de R$ 100 no mercado."

        ↓

IA identifica a intenção

        ↓

persist-transaction

        ↓

PersistTransactionUseCase

        ↓

TransactionRepository

        ↓

Banco de dados
```

### Tratamento de dinheiro

Também aprendi a importância de diferenciar o valor armazenado internamente do valor apresentado ao usuário.

Neste projeto, os valores são trabalhados em centavos e convertidos para reais na saída utilizando `BigDecimal`.

### Validação

Aprendi que validações importantes devem acontecer antes da persistência dos dados.

Além disso, aprendi a utilizar testes para garantir que:

* entradas inválidas sejam rejeitadas;
* o banco não seja chamado quando os dados são inválidos;
* entradas válidas continuem funcionando.

### Testes unitários

O desenvolvimento dos testes ajudou a entender melhor a utilização de:

* JUnit;
* Mockito;
* AssertJ;
* mocks;
* `verify`;
* `assertThatThrownBy`.

---

# Estrutura do projeto

```text
SantanderDIO_JavaAI/
│
├── gradle/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/budgeting/
│   │   │       ├── application/
│   │   │       ├── domain/
│   │   │       └── infrastructure/
│   │   │
│   │   └── resources/
│   │
│   └── test/
│       └── java/
│
├── compose.yml
├── build.gradle
├── gradlew
├── gradlew.bat
├── settings.gradle
└── README.md
```

---

# Melhorias realizadas

As principais melhorias realizadas neste desafio foram:

- Implementação de validação do valor da transação;
- Bloqueio de valores zero ou negativos;
- Validação da descrição;
- Validação da categoria;
- Testes unitários das validações;
- Teste do fluxo válido;
- Tratamento de valores monetários com `BigDecimal`;
- Conversão de centavos para reais;
- Cálculo do resumo financeiro;
- Testes do resumo financeiro;
- Tratamento de lista de transações vazia.

---

#  Status do projeto

Projeto desenvolvido como parte do **Santander Bootcamp — Java AI**, com melhorias próprias realizadas após a implementação do projeto-base.

A proposta foi utilizar o projeto como oportunidade para praticar:

**Java + Spring Boot + Spring AI + APIs REST + persistência + IA + Tool Calling + testes automatizados.**

---




