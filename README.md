# 📚 MyWebLib

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)


O projeto foi desenvolvido com foco em **Arquitetura de Software**, aplicando princípios de **SOLID**, **Clean Code** e **Padrões de Projeto**.

---

## 🚀 Tecnologias Utilizadas
### Frontend
Você pode acessar o frontend do projeto no seguinte repositório: https://github.com/leonardomenesesdev/myweblib-front
### Backend (API REST)
* **Linguagem:** Java
* **Framework:** Spring Boot
* **Segurança:** Spring Security + JWT
* **Persistência:** Spring Data JPA + Hibernate
* **Banco de Dados:** PostgreSQL
* **Ferramentas:** Maven, Lombok, Bean Validation

---

## ⚙️ Configuração e Instalação

### Pré-requisitos
* Java 21 JDK instalado
* Node.js (v20 ou superior)
* PostgreSQL instalado e rodando

### 1. Configuração do Banco de Dados
Crie um banco de dados no PostgreSQL com o nome desejado (ex: `scooby_doo_livro`).

### 2. Configuração do Backend
Como o arquivo `application.properties` contém dados sensíveis, ele não está versionado.

1.  Navegue até a pasta do backend.
2.  Crie o arquivo `src/main/resources/application.properties`.
3.  Preencha com suas credenciais baseando-se no modelo abaixo:

```properties
# Configurações do Banco de Dados
spring.datasource.url=jdbc:postgresql://localhost:5432/NOME_DO_SEU_BANCO
spring.datasource.username=SEU_USUARIO_POSTGRES
spring.datasource.password=SUA_SENHA_POSTGRES
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Configuração do Token JWT (Defina uma senha forte)
api.security.token.secret=${JWT_SECRET:minha-senha}
```
### 3. Execute o projeto!