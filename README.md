# Bitcoin Gold

Sistema de transações financeiras inspirado no funcionamento do Bitcoin, desenvolvido com foco em backend seguro, escalável e stateless.

---

## Descrição do Projeto

Bitcoin Gold é uma aplicação backend construída em Java 21 com Spring Boot que simula uma plataforma de transações financeiras baseada em criptomoedas. O sistema permite a criação de usuários, autenticação via JWT e execução de transações entre contas, garantindo segurança, integridade e alta performance.

O projeto adota arquitetura stateless para facilitar escalabilidade e integração com front-ends ou sistemas externos via API REST.

---

## Funcionalidades

- Registro e autenticação de usuários utilizando JSON Web Tokens (JWT)
- Realização de transações financeiras entre usuários
- Persistência dos dados com banco relacional via JPA/Hibernate
- API RESTful organizada em camadas (controller, service, repository)
- Segurança aplicada com Spring Security e validação de tokens
- Código modular e limpo, facilitando manutenção e testes futuros

---

## Tecnologias Utilizadas

- Java 21
- Spring Boot
- Spring Security
- JPA / Hibernate
- JWT (JSON Web Token)
- Maven

---

## Estrutura do Projeto

- `controller/` — Endpoints REST da aplicação
- `service/` — Lógica de negócio
- `repository/` — Integração com banco de dados via JPA
- `domain/` — Entidades do sistema
- `security/` — Configuração de segurança e JWT
- `dto/` — Objetos para transferência de dados entre camadas

---

## Contato

Pedro Henrique Silva  
[github.com/PedroHS-BR](https://github.com/PedroHS-BR)  
pedro.hsilva.pe@gmail.com

---

## Licença

Este projeto está licenciado sob a MIT License — veja o arquivo [LICENSE](LICENSE) para detalhes.
