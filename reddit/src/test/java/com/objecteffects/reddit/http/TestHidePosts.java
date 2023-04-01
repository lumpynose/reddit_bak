package com.objecteffects.reddit.http;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.objecteffects.reddit.http.data.Posts;
import com.objecteffects.reddit.main.Configuration;

public class TestHidePosts {
    private final static Logger log = LogManager
            .getLogger(TestHidePosts.class);

    @Test
    @SuppressWarnings("boxing")
    public void testPostMethod() throws IOException, InterruptedException {
        final List<String> users = Configuration.getHide();

        log.debug("configuration: {}", Configuration.dumpConfig());

        if (users.isEmpty()) {
            return;
        }

        final var getClient = new RedditGetMethod();

        for (final String user : users) {
            final var submittedMethod = String.format("/user/%s/submitted",
                    user);

            final var params = Map.of("limit", "300", "sort", "new", "type",
                    "links");

            final var methodResponse = getClient.getMethod(submittedMethod,
                    params);

            final var gson = new Gson();

            final Posts data = gson.fromJson(methodResponse.body(),
                    Posts.class);

            log.debug("data length: {}", data.getData().getChildren().size());

            final var postClient = new RedditPostMethod();

            final var hideMethod = String.format("/api/hide");

            for (final Posts.Post pd : data.getData().getChildren()) {
                log.debug("post: {}", pd.getPostData());

                final var param = Map.of("id", pd.getPostData().getName());

                final var hideResponse = postClient.postMethod(hideMethod,
                        param);

                log.debug("response: {}", hideResponse.statusCode());
            }
        }
    }
}
