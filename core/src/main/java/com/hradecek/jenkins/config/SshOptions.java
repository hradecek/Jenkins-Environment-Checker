package com.hradecek.jenkins.config;

import io.vertx.core.json.JsonObject;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:ivohradek@gmail.com">Ivo Hradek</a>
 */
public class SshOptions implements ConfigOptions {

    /**
     * TODO:
     */
    private static final Logger log = Logger.getLogger(SshOptions.class.getName());

    /**
     * TODO:
     */
    public static final String SSH_PROP = "ssh";

    /**
     * TODO:
     */
    public static final String PRV_KEY_SYS_PROP = "private.key";

    /**
     * TODO:
     */
    public static final String PUB_KEY_SYS_PROP = "public.key";

    /**
     * TODO:
     */
    public static final String PRV_KEY_CONF_KEY = "privateKey";

    /**
     * TODO:
     */
    public static final String PUB_KEY_CONF_KEY = "publicKey";

    private JsonObject config;

    private JsonObject checkOptions;

    /**
     * TODO:
     *
     * @param config
     * @param checkOptions
     */
    public SshOptions(JsonObject config, JsonObject checkOptions) {
        this.config = config;
        this.checkOptions = checkOptions;
    }

    /**
     * TODO:
     *
     * @return
     */
    @Override
    public JsonObject getJson() {
        Set<JsonObject> allSshOptions = getMultipleSshOptions(config, checkOptions);
        if (allSshOptions.isEmpty()) {
            throw new ConfigException("SSH key pair is not defined");
        }
        if (allSshOptions.size() > 1) {
            log.warning("There is multiple definition for SSH key pair. Using first by priorities:\n" +
                    " - System Property (-D" + PRV_KEY_SYS_PROP + ", -D" + PUB_KEY_CONF_KEY + ")\n" +
                    " - Local in config for specific check (" + PRV_KEY_CONF_KEY +", " + PUB_KEY_CONF_KEY + ")\n" +
                    " - Global in config. (" + PRV_KEY_CONF_KEY +", " + PUB_KEY_CONF_KEY + ")");
        }

        return allSshOptions.iterator().next();
    }

    /**
     * TODO:
     *
     * @param config
     * @param checkOptions
     * @return
     */
    private Set<JsonObject> getMultipleSshOptions(JsonObject config, JsonObject checkOptions) {
        Set<JsonObject> sshOptions = new HashSet<>();
        if (systemPropertyContainsSsh()) {
            sshOptions.add(createSshOptions(System.getProperty(PRV_KEY_SYS_PROP), System.getProperty(PUB_KEY_SYS_PROP)));
        }
        if (jsonContainsSsh(checkOptions)) {
            sshOptions.add(createSshOptions(checkOptions.getJsonObject(SSH_PROP)));
        }
        if (jsonContainsSsh(config)) {
            sshOptions.add(createSshOptions(config.getJsonObject(SSH_PROP)));
        }

        return sshOptions;
    }

    /**
     * TODO:
     *
     * @return
     */
    private boolean systemPropertyContainsSsh() {
        Properties sysProps = System.getProperties();
        return sysProps.containsKey(PRV_KEY_SYS_PROP) && sysProps.containsKey(PUB_KEY_SYS_PROP);
    }

    /**
     * TODO:
     *
     * @return
     */
    private boolean jsonContainsSsh(JsonObject config) {
        JsonObject sshJson = config.getJsonObject(SSH_PROP);

        return sshJson != null &&
                sshJson.containsKey(PRV_KEY_CONF_KEY) &&
                sshJson.containsKey(PUB_KEY_CONF_KEY);
    }

    /**
     * TODO:
     *
     * @return
     */
    private JsonObject createSshOptions(String privateKey, String publicKey) {
        JsonObject keyPair = new JsonObject().put(PRV_KEY_CONF_KEY, privateKey).put(PUB_KEY_CONF_KEY, publicKey);

        return createSshOptions(keyPair);
    }

    /**
     * TODO:
     *
     * @return
     */
    private JsonObject createSshOptions(JsonObject keyPair) {
        return new JsonObject().put("ssh", keyPair);
    }
}
