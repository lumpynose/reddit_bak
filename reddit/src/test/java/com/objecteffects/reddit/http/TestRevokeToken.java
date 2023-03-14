package com.objecteffects.reddit.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

public class TestRevokeToken {
    private final static Logger log = LogManager
            .getLogger(TestRevokeToken.class);

    @Test
    public void testRevokeToken()
            throws IOException, InterruptedException {
        final var client = new HttpClientRedditOAuth();

        final var response = client.revokeToken();

        log.debug("revoke response status: {}",
                Integer.valueOf(response.statusCode()));
        log.debug("revoke response headers: {}", response.headers());
        log.debug("revoke response body: {}", response.body());

        @SuppressWarnings("unused")
        final var list = new ArrayList<String>();
        @SuppressWarnings("unused")
        final List<String> list2 = new ArrayList<>();
    }
}
