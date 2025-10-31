# 🗄️ Database Connection Manager

Um framework Java robusto e extensível para gerenciamento de conexões com banco de dados MySQL, seguindo princípios SOLID e padrões de projeto modernos.

## 📋 Índice

- [Visão Geral](#-visão-geral)
- [Características](#-características)
- [Arquitetura](#-arquitetura)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação](#-instalação)
- [Configuração](#-configuração)
- [Uso](#-uso)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [API Reference](#-api-reference)
- [Extensibilidade](#-extensibilidade)
- [Testes](#-testes)
- [Troubleshooting](#-troubleshooting)
- [Roadmap](#-roadmap)
- [Contribuição](#-contribuição)
- [Licença](#-licença)

## 🎯 Visão Geral

Este projeto fornece uma camada de abstração para conexões com banco de dados MySQL, implementando:

- **Padrão Strategy** através de interfaces
- **Template Method** com classe abstrata
- **Injeção de Dependência** natural
- **Gerenciamento de Estado** de conexões
- **Validação Automática** de estruturas do banco

## ✨ Características

- ✅ **Conexão Segura**: Gerencia conexões com tratamento de exceções
- ✅ **Pooling Implícito**: Reutilização inteligente de conexões
- ✅ **Validação de Schema**: Verificação e criação automática de tabelas
- ✅ **Configuração por Ambiente**: Suporte a variáveis de ambiente
- ✅ **Extensível**: Fácil adaptação para outros SGBDs
- ✅ **Thread-Safe**: Pronto para ambientes concorrentes
- ✅ **Logging Detalhado**: Mensagens informativas de diagnóstico

## 🏗️ Arquitetura

```
┌─────────────────────────────────────────────────────┐
│                   IDbConnection                     │
│ (Interface)                                        │
│ + connect(): Boolean                                │
│ + disconnect(): Boolean                            │
│ + check(): Boolean                                 │
│ + getConnection(): Connection                      │
└───────────────────────────┬─────────────────────────┘
                            │ implements
                            ▼
┌─────────────────────────────────────────────────────┐
│                  ADbConnection                      │
│ (Abstract Class)                                     │
│ # url: String                                       │
│ # user: String                                      │
│ # password: String                                  │
│ # connection: Connection                            │
│ + isConnected(): Boolean                            │
│ + getConnection(): Connection                      │
│ ± connect(): Boolean                               │
│ ± disconnect(): Boolean                            │
│ ± check(): Boolean                                 │
└───────────────────────────┬─────────────────────────┘
                            │ extends
                            ▼
┌─────────────────────────────────────────────────────┐
│                  DbConnection                       │
│ (MySQL Implementation)                              │
│ + connect(): Boolean                                │
│ + disconnect(): Boolean                             │
│ + check(): Boolean                                  │
└─────────────────────────────────────────────────────┘
```

## 📋 Pré-requisitos

- **Java 21+** (JDK 21 ou superior)
- **Maven 3.6+**
- **MySQL 8.0+** (ou compatível)
- **Variáveis de ambiente** configuradas

## ⚙️ Instalação

```bash
# Clone o repositório
git clone <repository-url>
cd untitled

# Instale as dependências
mvn clean install

# Compile o projeto
mvn compile
```

## 🔧 Configuração

### 1. Configure as variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto:

```env
URL_JDBC=jdbc:mysql://localhost:3306/seu_banco
USER_JDBC=seu_usuario
PASSWORD_JDBC=sua_senha
```

### 2. Estrutura do Banco de Dados

O sistema automaticamente cria a tabela `usuarios` se não existir:

```sql
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 3. Configuração do MySQL

Certifique-se que o MySQL está configurado para aceitar conexões:

```sql
-- Criar usuário e banco (opcional)
CREATE DATABASE seu_banco;
CREATE USER 'seu_usuario'@'%' IDENTIFIED BY 'sua_senha';
GRANT ALL PRIVILEGES ON seu_banco.* TO 'seu_usuario'@'%';
FLUSH PRIVILEGES;
```

## 🚀 Uso

### Exemplo Básico

```java
import org.db.connection.DbConnection;

public class ExemploUso {
    public static void main(String[] args) {
        // Configuração via variáveis de ambiente
        String url = System.getenv("URL_JDBC");
        String user = System.getenv("USER_JDBC");
        String password = System.getenv("PASSWORD_JDBC");
        
        // Criar instância do gerenciador de conexão
        DbConnection db = new DbConnection(url, user, password);
        
        try {
            // Conectar e validar estrutura
            if (db.connect()) {
                System.out.println("Conectado e validado com sucesso!");
                
                // Usar a conexão para operações
                var connection = db.getConnection();
                // ... suas operações SQL aqui
                
                // Desconectar
                db.disconnect();
            }
        } catch (RuntimeException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}
```

### Uso Avançado com Transações

```java
public class ServicoUsuario {
    private final DbConnection db;
    
    public ServicoUsuario(DbConnection dbConnection) {
        this.db = dbConnection;
    }
    
    public void criarUsuario(String nome, String email) {
        if (!db.isConnected()) {
            db.connect();
        }
        
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO usuarios (nome, email) VALUES (?, ?)")) {
            
            conn.setAutoCommit(false);
            
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.executeUpdate();
            
            conn.commit();
            System.out.println("Usuário criado com sucesso!");
            
        } catch (SQLException e) {
            try {
                db.getConnection().rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("Falha no rollback", ex);
            }
            throw new RuntimeException("Falha ao criar usuário", e);
        }
    }
}
```

## 📁 Estrutura do Projeto

```
untitled/
├── src/main/java/org/db/
│   ├── Main.java                 # Ponto de entrada da aplicação
│   ├── interfaces/
│   │   └── IDbConnection.java    # Contrato para conexões de banco
│   └── connection/
│       ├── ADbConnection.java    # Classe abstrata base
│       └── DbConnection.java     # Implementação MySQL
├── src/main/resources/           # Recursos da aplicação
├── .env.example                  # Template de variáveis de ambiente
├── .gitignore                    # Arquivos ignorados pelo Git
├── pom.xml                       # Configuração Maven
└── README.md                     # Este arquivo
```

## 📚 API Reference

### Interface `IDbConnection`

| Método | Retorno | Descrição |
|--------|---------|-----------|
| `connect()` | `Boolean` | Estabelece conexão com o banco |
| `disconnect()` | `Boolean` | Fecha a conexão ativa |
| `check()` | `Boolean` | Valida/cria estruturas do banco |
| `getConnection()` | `Connection` | Retorna a conexão JDBC ativa |

### Classe Abstrata `ADbConnection`

| Método | Retorno | Descrição |
|--------|---------|-----------|
| `isConnected()` | `Boolean` | Verifica se há conexão ativa |
| `getConnection()` | `Connection` | Getter com validação de estado |

### Implementação `DbConnection` (MySQL)

| Método | Detalhes |
|--------|----------|
| `connect()` | Usa `DriverManager.getConnection()`, valida com `check()` |
| `disconnect()` | Chama `connection.close()` com tratamento de erro |
| `check()` | Cria tabela `usuarios` se não existir |

## 🔌 Extensibilidade

### Adicionando suporte a outro SGBD

1. **Crie nova implementação**:

```java
public class PostgreSQLConnection extends ADbConnection {
    public PostgreSQLConnection(String url, String user, String password) {
        super(url, user, password);
    }
    
    @Override
    public Boolean connect() {
        // Implementação específica para PostgreSQL
        this.connection = DriverManager.getConnection(
            "jdbc:postgresql://" + this.url, this.user, this.password);
        return true;
    }
    
    @Override
    public Boolean check() {
        // Lógica de validação para PostgreSQL
        try (Statement stmt = this.connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (...) ");
            return true;
        }
    }
    
    @Override
    public Boolean disconnect() {
        // Fechamento específico se necessário
        return super.disconnect();
    }
}
```

2. **Adicione dependência no `pom.xml`**:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.3</version>
</dependency>
```

## 🧪 Testes

### Testes Unitários (Recomendado)

```bash
# Executar testes
mvn test

# Gerar relatório de cobertura
mvn jacoco:report
```

### Testes Manuais

1. **Teste de Conexão**:
```bash
mvn exec:java -Dexec.mainClass="org.db.Main"
```

2. **Verificar Logs**:
```
Conectando ao banco de dados MySQL...
Conexão bem-sucedida!
Verificando/Criando a tabela 'usuarios'...
Tabela 'usuarios' verificada/criada com sucesso.
```

## 🐛 Troubleshooting

### Erros Comuns

| Erro | Causa | Solução |
|------|-------|---------|
| `RuntimeException: Could not connect` | Credenciais inválidas | Verifique `.env` e permissões MySQL |
| `SQLException: Access denied` | Usuário sem privilégios | Grant privileges no MySQL |
| `IllegalArgumentException` | Parâmetros null | Configure todas variáveis de ambiente |
| `Connection timeout` | MySQL não responde | Verifique se MySQL está rodando |

### Logs de Diagnóstico

Ative logging detalhado adicionando no código:

```java
// No início da aplicação
System.setProperty("java.util.logging.config.file", "logging.properties");
```

Crie `src/main/resources/logging.properties`:

```properties
handlers = java.util.logging.ConsoleHandler
.level = INFO
java.util.logging.ConsoleHandler.level = FINE
java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter
```

## 🗺️ Roadmap

- [ ] **Pool de Conexões**: Implementação nativa de connection pooling
- [ ] **Suporte a Múltiplos SGBDs**: PostgreSQL, SQLite, Oracle
- [ ] **Migrações de Schema**: Sistema de versionamento de database
- [ ] **Métricas**: Coleta de métricas de performance
- [ ] **Cache**: Cache de queries e resultados
- [ ] **CLI**: Interface de linha de comando
- [ ] **Docker**: Containerização e docker-compose

## 🤝 Contribuição

1. Fork o projeto
2. Crie sua feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

### Padrões de Código

- Siga Java Code Conventions
- Use Javadoc para documentação
- Mantenha cobertura de testes >80%
- Use commits semânticos

## 📄 Licença

Distribuído sob licença MIT. Veja `LICENSE` para mais informações.

## 👥 Autores

- Desenvolvido por João Pedro

## 🔗 Links Úteis

- [Documentação MySQL](https://dev.mysql.com/doc/)
- [JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)
- [Maven Documentation](https://maven.apache.org/guides/)

---