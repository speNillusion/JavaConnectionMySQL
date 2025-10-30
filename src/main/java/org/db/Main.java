package org.db;

import io.github.cdimascio.dotenv.Dotenv;

public class Main {
    public static void main(String[] args) {
        public static final Dotenv dotenv = Dotenv.load();
        private static final String URL = dotenv.get("URL_JDBC");
        private static final String USER = dotenv.get("USER_JDBC");
        private static final String PASSWORD = dotenv.get("PASSWORD_JDBC");
    }
}