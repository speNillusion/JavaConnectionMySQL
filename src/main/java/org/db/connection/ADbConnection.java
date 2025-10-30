package org.db.connection;

import org.db.interfaces.IDbConnection;
import java.sql.Connection;
import java.sql.SQLException;

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

    /**
     * Create inserts in any table that has 'nome' and 'email' columns.
     * @param table The name of the table where data will be inserted (e.g., "usuarios").
     * @return true if the select is successful, false otherwise.
     */
    @Override
    public abstract Boolean select(String table);

    /**
     * Create inserts in any table that has 'nome' and 'email' columns.
     * @param table The name of the table where data will be inserted (e.g., "usuarios").
     * @param nome The user's name to be inserted.
     * @param email The user's email to be inserted.
     * @return true if the insert is successful, false otherwise.
     */
    @Override
    public abstract Boolean insert(String table, String nome, String email);

    /**
     * {@inheritDoc}
     * This method must be implemented by subclasses to verify or create
     * necessary database structures, such as tables.
     */
    @Override
    public abstract Boolean check();
}
