package org.db.connection;

import org.db.interfaces.IDbConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class DbConnection extends ADbConnection implements IDbConnection {
    private String URL = null;
    private String USER = null;
    private String PASSWORD = null;
    Connection client = null;

    public DbConnection(String URL, String USER, String PASSWORD) {
        try {
            this.URL = URL;
            this.USER = USER;
            this.PASSWORD = PASSWORD;

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }


    public Boolean connect() {
        boolean stage = false;
        try {
            System.out.println("Conectando ao banco de dados...");
            client = DriverManager.getConnection(this.URL, this.USER, this.PASSWORD);

            if (client != null) {
                System.out.println("Conexão bem-sucedida!");
                // Você pode executar suas consultas SQL aqui
                stage = true;
            }

        } catch (SQLException e) {
            System.err.println("Falha na conexão com o banco de dados.");
            throw new RuntimeException(e);
        }
        return stage;
    }

    public Boolean check() {
        return true;
    }

}
