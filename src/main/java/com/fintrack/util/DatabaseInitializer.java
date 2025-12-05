package com.fintrack.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

    private static final String DB_URL_NO_DB = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";
    private static final String DB_URL_WITH_DB = "jdbc:mysql://localhost:3306/fintrack360?useSSL=false&serverTimezone=UTC&allowMultiQueries=true";
    private static final String USER = "root";
    private static final String PASSWORD = "12345678";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initializing Database...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 1. Create Database if not exists
            try (Connection conn = DriverManager.getConnection(DB_URL_NO_DB, USER, PASSWORD);
                    Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS fintrack360");
                System.out.println("Database 'fintrack360' checked/created.");
            }

            // 2. Execute Schema (Tables)
            String schemaSql;
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("schema.sql")) {
                if (is == null) {
                    System.err.println("schema.sql not found!");
                    return;
                }
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    schemaSql = reader.lines().collect(Collectors.joining("\n"));
                }
            }

            // Remove comments and split by semicolon
            // This is a simple parser; might break if semicolons are in strings, but
            // schema.sql is simple.
            String[] statements = schemaSql.split(";");

            try (Connection conn = DriverManager.getConnection(DB_URL_WITH_DB, USER, PASSWORD);
                    Statement stmt = conn.createStatement()) {

                for (String sql : statements) {
                    String trimmedSql = sql.trim();
                    if (trimmedSql.isEmpty())
                        continue;

                    // Skip USE command if we are already connected to the DB
                    if (trimmedSql.toUpperCase().startsWith("USE "))
                        continue;
                    // Skip CREATE DATABASE as we already did it
                    if (trimmedSql.toUpperCase().startsWith("CREATE DATABASE "))
                        continue;

                    try {
                        System.out.println(
                                "Executing: " + trimmedSql.substring(0, Math.min(trimmedSql.length(), 50)) + "...");
                        stmt.execute(trimmedSql);
                    } catch (Exception e) {
                        System.err.println("Failed to execute statement: " + trimmedSql);
                        e.printStackTrace();
                    }
                }
                System.out.println("Database initialized successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to initialize database: " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup if needed
    }
}
