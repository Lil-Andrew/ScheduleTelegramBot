package org.example.utils;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.Main;

public class EnvConfig {
    private static final Dotenv dotenv = Main.DOTENV;

    public static String get(String key, Object... params) {
        String value = dotenv.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Not found: " + key);
        }
        return params.length > 0 ? String.format(value, params) : value;
    }
}
