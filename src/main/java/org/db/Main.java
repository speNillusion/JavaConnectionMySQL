package org.db;

import io.github.cdimascio.dotenv.Dotenv;
import org.db.connection.DbConnection;

public class Main {
    public static final Dotenv dotenv = Dotenv.load();
    private static final String URL = dotenv.get("URL_JDBC");
    private static final String USER = dotenv.get("USER_JDBC");
    private static final String PASSWORD = dotenv.get("PASSWORD_JDBC");


    public static void main(String[] args) {
        DbConnection client = new DbConnection(URL,USER,PASSWORD);

        try {
            Boolean clientConnect = client.connect();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}