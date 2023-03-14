package com.objecteffects.reddit.http;

import java.io.IOException;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

public class TestGetMethodMe {
    private final static Logger log = LogManager
            .getLogger(TestGetMethodMe.class);

    @Test
    public void testGetMethod() throws IOException, InterruptedException {
        final var client = new HttpClientRedditOAuth();

        final var authResponse = client.getAuthToken();

        log.debug("auth response status: {}",
                Integer.valueOf(authResponse.statusCode()));
        log.debug("auth response headers: {}", authResponse.headers());
        log.debug("auth response body: {}", authResponse.body());

        final var methodResponse = client
                .getMethod("api/v1/me", Collections.emptyMap());

        log.debug("method response status: {}",
                Integer.valueOf(methodResponse.statusCode()));
        log.debug("method response headers: {}", methodResponse.headers());
        log.debug("method response body: {}", methodResponse.body());

        final var revokeResponse = client.revokeToken();

        log.debug("revoke response status: {}",
                Integer.valueOf(revokeResponse.statusCode()));
        log.debug("revoke response headers: {}", revokeResponse.headers());
        log.debug("revoke response body: {}", revokeResponse.body());
    }
}
