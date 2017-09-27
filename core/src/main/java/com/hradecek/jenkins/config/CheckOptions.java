package com.hradecek.jenkins.config;

import io.vertx.core.json.JsonObject;

/**
 * TODO:
 *
 * @author <a href="mailto:ivohradek@gmail.com">Ivo Hradek</a>
 */
public class CheckOptions implements ConfigOptions {

    /**
     * Constant string representing system property or JSON key defining check classes.
     */
    public static final String CHECKS_PROP = "checks";

    private JsonObject config;

    private String className;

    /**
     * TODO
     *
     * @param config
     * @param className
     */
    public CheckOptions(JsonObject config, String className) {
        this.config = config;
        this.className = className;
    }

    /**
     * TODO
     *
     * @return
     */
    @Override
    public JsonObject getJson() {
        return config.getJsonObject(CHECKS_PROP).getJsonObject(className);
    }
}
