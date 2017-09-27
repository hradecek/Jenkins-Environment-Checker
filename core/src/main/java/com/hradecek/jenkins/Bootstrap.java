package com.hradecek.jenkins;

import com.hradecek.jenkins.check.CheckVerticle;
import com.hradecek.jenkins.config.ConfigOptions;
import com.hradecek.jenkins.config.Configuration;
import com.hradecek.jenkins.config.SshOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import org.apache.commons.lang3.tuple.Pair;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.hradecek.jenkins.config.CheckOptions;
import rx.Observable;

import java.security.Security;
import java.util.logging.Logger;

import static com.hradecek.jenkins.config.CheckOptions.CHECKS_PROP;

/**
 * Bootstrap class, which is responsible for parsing arguments, system properties,
 * initializing <i>Vert.x</i>, loading check verticles, etc. This class is
 * also entry point of whole java application.
 *
 * @see CheckVerticle
 * @author <a href="mailto:ivohradek@gmail.com">Ivo Hradek</a>
 */
public final class Bootstrap {

    /*
     * Solves 'DH key size must be multiple of 64 ...' issue
     */
    static {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    /**
     * Bootstrap Vert.x instance
     */
    private static final Vertx vertx = Vertx.vertx();

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(Bootstrap.class.getName());

    /**
     * <p>Bootstrap method. Set-up and loads all checks.
     *
     * <p>Deploy all checks passed via {@code -Dchecks} system property. Corresponding options from
     * config file, will be passed along with check verticle as {@link DeploymentOptions}.
     *
     * <p>Verticle options has to be defined in config
     * file under its fully classified name.
     *
     * @param args command line arguments
     */
    public static void main(String... args) {
        Observable<String> checks = (null == System.getProperty(CHECKS_PROP)) ? handleConfig() : handleSystemProperty();
        checks.flatMap(Bootstrap::readOptionsByVerticle).flatMap(Bootstrap::deployCheck).subscribe();
    }

    private static Observable<String> deployCheck(Pair<String, DeploymentOptions> pair) {
        return vertx.rxDeployVerticle(pair.getLeft(), pair.getRight()).toObservable();
    }

    /**
     * <p>Reads config file and return observable of all keys under {@code checks} key.
     *
     * <p>This method should be used for parsing check verticles defined by their full
     * class names (e.g. {@code com.hradecek.jenkins.check.entropy.EntropyVerticle}).
     *
     * @return string observable of check class' names
     */
    private static Observable<String> handleConfig() {
        return Configuration.readConfig(vertx).map(config -> config.getJsonObject(CHECKS_PROP).getMap().keySet())
                           .flatMapObservable(Observable::from);
    }

    /**
     * <p>Reads system property {@code -Dchecks} and
     * return observable of strings split by ";".
     *
     * <p>This method should be used for parsing check verticles defined by their full
     * class names (e.g. {@code com.hradecek.jenkins.check.entropy.EntropyVerticle}).
     *
     * @return string observable of check class' names
     */
    private static Observable<String> handleSystemProperty() {
        return Observable.from(System.getProperty(CHECKS_PROP).split(";")).filter(prop -> !prop.isEmpty());
    }

    /**
     * Reads check verticle options from config file. Check verticle is defined by its class name.
     * In case of there are no options defined, empty config will be returned (not null).
     *
     * @param className verticle which options should be read
     * @return Observable, pair of check verticle class name with its options
     */
    private static Observable<Pair<String, DeploymentOptions>> readOptionsByVerticle(final String className) {
        return Configuration.readConfig(vertx).map(config -> getDeploymentOptions(config, className)).toObservable();
    }

    private static Pair<String, DeploymentOptions> getDeploymentOptions(JsonObject config, String className) {
        ConfigOptions checkOptions = new CheckOptions(config, className);
        ConfigOptions sshOptions = new SshOptions(config, checkOptions.getJson());
        Configuration configuration = new Configuration().add(checkOptions).add(sshOptions);

        log.info("Deployment options (" + className + "):\n" + configuration.getJson().encodePrettily());
        DeploymentOptions options = new DeploymentOptions().setConfig(configuration.getJson());

        return Pair.of(className, options);
    }
}
