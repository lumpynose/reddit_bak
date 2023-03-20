package com.objecteffects.reddit.http;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.objecteffects.reddit.main.Configuration;

public class TestHidePosts {
    private final static Logger log = LogManager
            .getLogger(TestHidePosts.class);

    @Test
    @SuppressWarnings("boxing")
    public void testPostMethod() throws IOException, InterruptedException {
        final String user = Configuration.getHide();

        final var getClient = new RedditGetMethod();

        final var submittedMethod = String.format("/user/%s/submitted", user);

        final var params = Map.of("limit", "50", "sort", "new", "type",
                "links");

        final var methodResponse = getClient.getMethod(submittedMethod, params);

        final var gson = new Gson();

        final Posts data = gson.fromJson(methodResponse.body(), Posts.class);

        log.debug("data length: {}",
                data.getData().getChildren().size());

        final var postClient = new RedditPostMethod();

        final var hideMethod = String.format("/api/hide", user);

        for (final Posts.Post pd : data.getData().getChildren()) {
            log.debug("name: {}", pd.getPostData());

            final var param = Map.of("id", pd.getPostData().getName());

            final var hideResponse = postClient.postMethod(hideMethod, param);

            log.debug("response: {}", hideResponse.statusCode());
        }
    }
}
