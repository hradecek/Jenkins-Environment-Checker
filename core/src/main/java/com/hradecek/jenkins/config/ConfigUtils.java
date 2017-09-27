package com.hradecek.jenkins.config;

import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Ability to from information from config file outside Vert.x instance, e.g. in reuslts classes.
 *
 * @author <a href="mailto:ivohradek@gmail.com">Ivo Hradek</a>
 */
public class ConfigUtils {

    /**
     * JSON representing config.
     */
    private static final JsonObject config = readConfig();

    private ConfigUtils() { }

    private static JsonObject readConfig() {
        String stringConfig = "";
        try {
            Files.lines(Paths.get(Configuration.CONFIG_PATH)).forEach(line -> line += stringConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JsonObject(stringConfig);
    }

    public static String getString(Class klazz, String key) {
        return getCheckOptions(klazz).getString(key);
    }

    public static int getInt(Class klazz, String key) {
        return getCheckOptions(klazz).getInteger(key);
    }

    private static JsonObject getCheckOptions(Class klazz) {
        return config.getJsonObject(CheckOptions.CHECKS_PROP).getJsonObject(klazz.getName());
    }
}
