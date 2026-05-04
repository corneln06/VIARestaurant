package org.store.viarestaurant.config;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        try (
                Connection conn = DatabaseConnection.getConnection();
                InputStream is = DatabaseInitializer.class
                        .getResourceAsStream("/db/schema.sql")
        ) {

            if (is == null) {
                throw new RuntimeException("schema.sql not found in resources/db/");
            }

            String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
            }

            System.out.println("✅ Database initialized");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}