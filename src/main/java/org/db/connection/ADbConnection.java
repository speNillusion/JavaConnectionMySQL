package org.db.connection;

import org.db.interfaces.IDbConnection;

import java.sql.*;

/**
 * Abstract base class for database connections.
 * It manages connection details, the connection object, and defines the core contract for connection handling.
 * @version 1.1.0
 */
public abstract class ADbConnection implements IDbConnection {

    protected final String url;
    protected final String user;
    protected final String password;
    protected Connection connection = null;

    /**
     * Constructor to initialize the connection details.
     * @param url The full JDBC URL for the database.
     * @param user The database username.
     *param password The database user password.
     */
    public ADbConnection(String url, String user, String password) {
        if (url == null || user == null || password == null) {
            throw new IllegalArgumentException("URL, USER, and PASSWORD cannot be null.");
        }
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Provides the active database connection object.
     * Throws a RuntimeException if the connection is not active or has been closed.
     * @return The active SQL Connection object.
     */
    @Override
    public Connection getConnection() {
        if (!isConnected()) {
            throw new RuntimeException("Connection is not active. Please call connect() before getting the connection.");
        }
        return this.connection;
    }

    /**
     * Checks if the connection is currently active and valid.
     * @return true if the connection is not null and not closed, false otherwise.
     */
    public boolean isConnected() {
        try {
            return this.connection != null && !this.connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Error checking connection status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Selects and displays all records from a table.
     * Assumes the table has at least 'id', 'nome', and 'email' columns.
     * @param table The name of the table to query (e.g., "usuarios").
     * @return true if the select is successful and prints results, false if an error occurs.
     */
    @Override
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
            e.notifyAll(); // Importante para depuração
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
    @Override
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

    // --- Métodos Abstratos a serem implementados pelas classes filhas ---

    /**
     * {@inheritDoc}
     * This method must be implemented by subclasses to establish a connection
     * to a specific database (e.g., MySQL, PostgreSQL).
     */
    @Override
    public abstract Boolean connect();

    /**
     * {@inheritDoc}
     * This method must be implemented by subclasses to properly close the
     * database connection and release resources.
     */
    @Override
    public abstract Boolean disconnect();
}
