package org.store.viarestaurant.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class EnvLoader {

    private static final Map<String, String> env = new HashMap<>();

    static {
        try (BufferedReader br = new BufferedReader(new FileReader(".env"))) {
            br.lines().forEach(line -> {
                if (!line.startsWith("#") && line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    env.put(parts[0].trim(), parts[1].trim());
                }
            });
        } catch (Exception e) {
            System.out.println("⚠️ .env file not found");
        }
    }

    public static String get(String key) {
        return env.get(key);
    }
}