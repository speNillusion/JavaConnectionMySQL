package org.db.connection;

import java.sql.*;

/**
 * MySQL-specific implementation of a database connection.
 * This class handles the logic for connecting to, disconnecting from, and checking a MySQL database.
 * @version 1.1.0
 */
public class DbConnection extends ADbConnection {

    /**
     * Constructor that passes the connection details to the abstract parent class.
     * @param URL The full JDBC URL for the MySQL database.
     * @param USER The database username.
     * @param PASSWORD The database user password.
     */
    public DbConnection(String URL, String USER, String PASSWORD) {
        super(URL, USER, PASSWORD); // Chama o construtor da classe pai (ADbConnection)
    }

    @Override
    public Boolean connect() {
        if (isConnected()) {
            System.out.println("A conexão já está ativa.");
            return true;
        }

        try {
            System.out.println("Conectando ao banco de dados MySQL...");
            // As propriedades url, user e password são herdadas da classe pai
            this.connection = DriverManager.getConnection(this.url, this.user, this.password);
            System.out.println("Conexão bem-sucedida!");
            try {
                this.check();

            } catch (RuntimeException e) {
                throw new RuntimeException("Could not check to the database", e);
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Falha na conexão com o banco de dados.");
            throw new RuntimeException("Could not connect to the database", e);
        }
    }

    @Override
    public Boolean disconnect() {
        if (!isConnected()) {
            System.out.println("Nenhuma conexão ativa para fechar.");
            return true;
        }
        try {
            System.out.println("Fechando a conexão com o banco de dados...");
            this.connection.close();
            System.out.println("Conexão fechada com sucesso.");
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao fechar a conexão com o banco de dados.");
            e.notify();
            return false;
        }
    }

    /**
     * Selects and displays all records from a table.
     * Assumes the table has at least 'id', 'nome', and 'email' columns.
     * @param table The name of the table to query (e.g., "usuarios").
     * @return true if the select is successful and prints results, false if an error occurs.
     */
    public Boolean select(String table) {
        if (!isConnected()) {
            System.err.println("Não é possível buscar dados. A conexão com o banco de dados não está ativa.");
            return false;
        }


        String selectSQL = String.format("SELECT id, nome, email FROM %s", table);

        System.out.println("Executando busca de dados na tabela: " + table);

        // 3. Usar try-with-resources para PreparedStatement e ResultSet
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(selectSQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("--- Resultados da Tabela: " + table + " ---");
            boolean foundResults = false;

            // 5. Iterar sobre o ResultSet
            while (resultSet.next()) {
                foundResults = true;
                // 6. Extrair os dados de cada coluna para a linha atual
                int id = resultSet.getInt("id");
                String nome = resultSet.getString("nome");
                String email = resultSet.getString("email");

                // 7. Exibir os dados formatados
                System.out.printf("ID: %-5d | Nome: %-20s | Email: %s\n", id, nome, email);
            }

            if (!foundResults) {
                System.out.println("Nenhum registro encontrado na tabela.");
            }

            System.out.println("----------------------------------------");
            return true;

        } catch (SQLException e) {
            System.err.println("Falha ao executar o comando SELECT na tabela '" + table + "'.");
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace(); // Importante para depuração
            return false;
        }
    }


    /**
     * Create inserts in any table that has 'nome' and 'email' columns.
     * @param table The name of the table where data will be inserted (e.g., "usuarios").
     * @param nome The user's name to be inserted.
     * @param email The user's email to be inserted.
     * @return true if the insert is successful, false otherwise.
     */
    public Boolean insert(String table, String nome, String email) {
        if (!isConnected()) {
            System.err.println("Não é possível inserir dados. A conexão com o banco de dados não está ativa.");
            return false;
        }

        String insertSQL = String.format("INSERT INTO %s (nome, email) VALUES (?, ?)", table);

        System.out.println("Preparando a inserção de dados na tabela: " + table);

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, nome);
            preparedStatement.setString(2, email);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Dados inseridos com sucesso! Linhas afetadas: " + rowsAffected);
                return true;
            } else {
                System.err.println("A inserção falhou, nenhuma linha foi alterada.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Falha ao executar o comando de inserção na tabela '" + table + "'.");

            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            e.notify();
            return false;
        }
    }



    @Override
    public Boolean check() {
        if (!isConnected()) {
            System.err.println("Não é possível verificar as tabelas. A conexão não está ativa.");
            return false;
        }

        try (Statement statement = this.connection.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nome VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL UNIQUE, " +
                    "data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";

            System.out.println("Verificando/Criando a tabela 'usuarios'...");
            statement.execute(createTableSQL);
            System.out.println("Tabela 'usuarios' verificada/criada com sucesso.");
            return true;
        } catch (SQLException e) {
            System.err.println("Falha ao verificar/criar a tabela 'usuarios'.");
            e.notify();
            return false;
        }
    }
}
