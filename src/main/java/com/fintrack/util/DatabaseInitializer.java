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

    private static final String DB_URL_NO_DB = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowMultiQueries=true";
    private static final String USER = "root";
    private static final String PASSWORD = "12345678"; // Matches user's update

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initializing Database...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Read schema.sql
            InputStream is = getClass().getClassLoader().getResourceAsStream("schema.sql");
            if (is == null) {
                System.err.println("schema.sql not found!");
                return;
            }
            String schemaSql = new BufferedReader(new InputStreamReader(is))
                    .lines().collect(Collectors.joining("\n"));

            // Connect and Execute
            try (Connection conn = DriverManager.getConnection(DB_URL_NO_DB, USER, PASSWORD);
                    Statement stmt = conn.createStatement()) {

                // Execute the script (which includes CREATE DATABASE)
                // We need allowMultiQueries=true in URL for this to work with multiple
                // statements
                stmt.execute(schemaSql);
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
