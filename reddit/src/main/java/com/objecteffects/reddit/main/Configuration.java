package com.objecteffects.reddit.main;

import java.io.File;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Configuration {
    private final static Logger log = LogManager
            .getLogger(Configuration.class);

    private static String username = null;
    private static String password = null;
    private static String clientId = null;
    private static String secret = null;
    private static String oauthToken = null;

    public static void loadConfiguration() {
        final var configs = new Configurations();

        try {
            final PropertiesConfiguration config = configs
                    .properties(
                            new File("c:/home/rusty/redditconfig.properties"));

            username = config.getString("username");
            password = config.getString("password");

            clientId = config.getString("client_id");
            secret = config.getString("secret");
        }
        catch (final ConfigurationException e) {
            log.error("configuration exception: ", e);

            username = "";
            password = "";
            clientId = "";
            secret = "";
        }
    }

    public static String getUsername() {
        if (username == null) {
            loadConfiguration();
        }

        return username;
    }

    public static String getPassword() {
        if (password == null) {
            loadConfiguration();
        }

        return password;
    }

    public static String getClientId() {
        if (clientId == null) {
            loadConfiguration();
        }

        return clientId;
    }

    public static String getSecret() {
        if (secret == null) {
            loadConfiguration();
        }

        return secret;
    }

    public static String getOAuthToken() {
        return oauthToken;
    }

    public static void setOAuthToken(final String _oauthToken) {
        oauthToken = _oauthToken;
    }

}
