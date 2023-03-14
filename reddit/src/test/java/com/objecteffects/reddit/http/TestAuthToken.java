package com.objecteffects.reddit.http;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.objecteffects.reddit.main.Configuration;

public class TestAuthToken {
    private final static Logger log = LogManager
            .getLogger(TestAuthToken.class);

    @Test
    public void testGetAuthToken() throws IOException, InterruptedException {

        final var client = new HttpClientRedditOAuth();

        final var response = client.getAuthToken();

        log.debug("response status: {}",
                Integer.valueOf(response.statusCode()));
        log.debug("response headers: {}", response.headers());
        log.debug("response body: {}", response.body());

        final var access_token = Configuration.getOauthToken();

        log.debug("access_token: {}", access_token);
    }
}
