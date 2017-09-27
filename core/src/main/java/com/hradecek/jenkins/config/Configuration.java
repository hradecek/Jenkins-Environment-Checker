package com.hradecek.jenkins.config;

import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import rx.Single;

import java.util.HashSet;
import java.util.Set;

/**
 * Class representing check configuration and utils for its manipulation.
 *
 * @author <a href="mailto:ivohradek@gmail.com">Ivo Hradek</a>
 */
public class Configuration implements ConfigOptions {

    /**
     * Config path. Custom config may be passed via {@code -Dconf} system property.
     */
    public static final String CONFIG_PATH = System.getProperty("conf", "conf/config.json");

    /**
     * Contains all options.
     */
    private Set<ConfigOptions> options = new HashSet<>();

    @Override
    public JsonObject getJson() {
        JsonObject merged = new JsonObject();
        for (ConfigOptions option : options) {
            merged.mergeIn(option.getJson());
        }

        return merged;
    }

    public Configuration add(ConfigOptions configOptions) {
        options.add(configOptions);
        return this;
    }

    /**
     * Reads main JSON config file defined by {@code -Dconf} system
     * property or by default value {@code conf/config.json)}
     *
     * @return json representing configuration
     */
    public static Single<JsonObject> readConfig(Vertx vertx) {
        return vertx.fileSystem().rxReadFile(CONFIG_PATH).map(Buffer::toJsonObject);
    }
}
