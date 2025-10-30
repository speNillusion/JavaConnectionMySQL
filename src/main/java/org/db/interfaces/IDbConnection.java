package org.db.interfaces;

import java.sql.Connection;

/**
 * Interface that defines the contract for database connection classes.
 * @version 1.0.0
 */
public interface IDbConnection {
    /**
     * Establishes a connection to the database.
     * @return true if the connection is successful, false otherwise.
     */
    Boolean connect();

    /**
     * Closes the database connection.
     * @return true if the disconnection is successful, false otherwise.
     */
    Boolean disconnect();

    /**
     * Checks if the required database structures (like tables) exist, and creates them if they don't.
     * @return true if the structures exist or were created successfully, false otherwise.
     */
    Boolean check();

    /**
     * Provides the active database connection object.
     * @return The active SQL Connection object.
     */
    Connection getConnection();
}
