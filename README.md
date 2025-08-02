# 💰 Bitcoin Gold: Plataforma de Simulação de Criptomoeda

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**Bitcoin Gold** é uma aplicação de backend robusta, desenvolvida em **Java 21 com Spring Boot 3**, que simula um sistema de transações financeiras inspirado no funcionamento da blockchain do Bitcoin. O projeto foi arquitetado para ser seguro, escalável e *stateless*, expondo uma API RESTful para interações.


---

## 📚 Sumário

- [🚀 Aplicação Online (Demo)](#-aplicação-online-demo)
- [📖 Visão Geral](#-visão-geral)
- [✨ Funcionalidades Principais](#-funcionalidades-principais)
- [🛠️ Tecnologias Utilizadas](#tecnologias-utilizadas)
- [🚀 Como Executar o Projeto Localmente](#-como-executar-o-projeto-localmente)
- [👨‍💻 Contato](#-contato)
- [📄 Licença](#-licença)

---
## 🚀 Aplicação Online (Demo)

O projeto está disponível para acesso e testes no seguinte endereço:

* **URL da Aplicação:** [https://bitcoin-gold.onrender.com](https://bitcoin-gold.onrender.com)  
* **Documentação (Swagger):** [https://bitcoin-gold.onrender.com/swagger-ui.html](https://bitcoin-gold.onrender.com/swagger-ui.html)

> **Atenção:** A aplicação está hospedada em um plano gratuito. Ao acessá-la pela primeira vez, **pode levar de 1 a 3 minutos para o servidor iniciar**. Por favor, aguarde esse período inicial. Após a inicialização, a API funcionará normalmente.

Para testar os endpoints que exigem autenticação, siga estes passos na [página da documentação](https://bitcoin-gold.onrender.com/swagger-ui.html):
1. Use o endpoint `POST /auth/login` para obter um token JWT.
2. Clique no botão **`Authorize`**, no canto superior direito.
3. Na janela que abrir, cole o seu token no campo "Value", prefixado por `Bearer ` (ex: `Bearer seu.token.jwt`).
4. Clique em `Authorize` para validar a sessão. Agora você pode testar todos os endpoints protegidos.

---

## 📖 Visão Geral

O projeto implementa os conceitos fundamentais de uma criptomoeda, incluindo:

* **Usuários e Carteiras:** Cada usuário possui uma carteira digital segura, com saldo e capacidade de transacionar.
* **Transações:** As transferências de valores entre carteiras são registradas como transações pendentes.
* **Mineração e Blockchain:** As transações pendentes são agrupadas em blocos que, por meio de um processo de mineração (*Proof-of-Work*), são validados e adicionados à blockchain, tornando os registros imutáveis.
* **Segurança:** A autenticação é gerenciada via **JSON Web Tokens (JWT)**, e as operações críticas são protegidas por papéis (roles) de usuário, garantindo que apenas usuários autorizados possam executar determinadas ações.

A arquitetura do sistema segue o padrão de camadas (Controller, Service, Repository), promovendo baixo acoplamento e alta coesão, o que facilita a manutenção e a escalabilidade.

---

## ✨ Funcionalidades Principais

* **Autenticação e Autorização:**
  * Registro de novos usuários com criação automática de carteira.
  * Login seguro com geração de token JWT para autenticação stateless.
  * Controle de acesso baseado em papéis (`USER`, `ADMIN`) com Spring Security.
  * **Endpoints Protegidos:** Com exceção das rotas de autenticação (`/auth/**`), todos os outros endpoints requerem um token JWT válido no cabeçalho `Authorization`.

* **Gestão de Transações:**
  * Criação de transações financeiras entre usuários.
  * Validação de saldo e regras de negócio (ex: não transferir para si mesmo).
  * Listagem de transações (gerais, por usuário e pendentes).

* **Blockchain e Mineração:**
  * Criação de um **bloco Gênesis** na inicialização do sistema.
  * Endpoint de mineração que executa o *Proof-of-Work* (hash começando com "0000").
  * Validação da integridade da blockchain e dos blocos individuais.
  * Recompensa de mineração creditada na carteira do minerador.

* **Painel Administrativo:**
  * Endpoints exclusivos para administradores para gerenciar usuários (CRUD).

* **Segurança de Dados:**
  * Criptografia de IDs de carteira usando **AES** para ofuscar dados sensíveis.
  * Hashing de senhas com **BCrypt**.

---

## 🛠️ Tecnologias Utilizadas

| Categoria              | Tecnologias                                    |
|------------------------|------------------------------------------------|
| **Linguagem**          | Java 21                                        |
| **Framework**          | Spring Boot 3                                  |
| **Segurança**          | Spring Security, JWT (JSON Web Token)         |
| **Banco de Dados**     | PostgreSQL (prod/dev), H2 (testes)             |
| **ORM**                | Spring Data JPA, Hibernate                     |
| **Build Tool**         | Maven                                          |
| **Containerização**    | Docker, Docker Compose                         |
| **Documentação API**   | SpringDoc OpenAPI (Swagger UI)                 |
| **Outras Bibliotecas** | Lombok, MapStruct                              |


---

## 🚀 Como Executar o Projeto Localmente

Você pode executar a aplicação localmente utilizando Docker (recomendado) ou configurando o ambiente manualmente.

### ✅ Pré-requisitos

* JDK 21  
* Maven 3.9+  
* Docker e Docker Compose

---

### 🔹 1. Executando com Docker (Recomendado)

A maneira mais simples de iniciar o projeto é com o Docker Compose, que subirá um container com a aplicação e outro com o banco de dados PostgreSQL.

#### Passo 1: Crie o arquivo `docker-compose.yml`

```yaml
version: "3.8"
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_DB: bitcoingoldpostgres
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: examplepassword
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

volumes:
  pgdata:
```

#### Passo 2: Configure as variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto com o seguinte conteúdo:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/bitcoingoldpostgres
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=examplepassword
JWT_SECRET=sua-chave-secreta-super-segura-para-jwt
CRYPTO_KEY=uma-chave-de-16-bytes-para-aes
```

> ⚠️ **Atenção:**  
> - `CRYPTO_KEY` deve ter **exatamente 16 caracteres** (16 bytes).  
> - `JWT_SECRET` deve ser uma string longa e segura.

#### Passo 3: Compile e Execute

```bash
# 1. Compile o projeto com Maven
./mvnw clean package

# 2. Inicie os containers em modo detached
docker-compose up -d --build
```

A aplicação estará disponível em: [http://localhost:8080](http://localhost:8080)

---

### 🔹 2. Executando Manualmente

#### Passo 1: Inicie um banco de dados PostgreSQL

Garanta que você tenha uma instância do PostgreSQL rodando e crie um banco de dados (ex: `bitcoingoldpostgres`).

#### Passo 2: Configure as variáveis de ambiente

Crie o arquivo `.env` como no exemplo anterior, ajustando o `SPRING_DATASOURCE_URL` para apontar para sua instância local:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bitcoingoldpostgres
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=examplepassword
JWT_SECRET=sua-chave-secreta-super-segura-para-jwt
CRYPTO_KEY=uma-chave-de-16-bytes-para-aes
```

#### Passo 3: Execute a Aplicação

```bash
# Compile o projeto
./mvnw clean package

# Execute o arquivo .jar gerado
java -jar target/bitcoin-gold-0.0.1-SNAPSHOT.jar
```

---

## 👨‍💻 Contato

**Pedro Henrique da Silva**

- 🐙 GitHub: [PedroHS-BR](https://github.com/PedroHS-BR)
- 💼 LinkedIn: [Pedro Henrique](https://www.linkedin.com/in/pedro-henrique/)
- 📧 Email: pedro.hsilva.pe@gmail.com


---

## 📄 Licença

Este projeto está licenciado sob a [Licença MIT](https://opensource.org/licenses/MIT).  
Consulte o arquivo `LICENSE` para mais detalhes.
