package com.scc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private DatabaseInitializer() {}

    public static void initialize() {
        String users = """
            CREATE TABLE IF NOT EXISTS users (
                id            INTEGER PRIMARY KEY AUTOINCREMENT,
                username      TEXT NOT NULL UNIQUE,
                password_hash TEXT NOT NULL,
                full_name     TEXT NOT NULL,
                role          TEXT NOT NULL,
                status        TEXT NOT NULL DEFAULT 'PENDING',
                created_at    TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            );
            """;
        // TODO: add the other tables from your ERD here (parent tables first)

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(users);
            new UserDao().seedAdmin("admin", UserDao.hash("admin123"), "System Administrator");
            System.out.println("[DB] Database initialized.");
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed: " + e.getMessage(), e);
        }
    }
}
