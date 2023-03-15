package com.objecteffects.reddit.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.objecteffects.reddit.main.Configuration;

public class RedditGetMethod {
    private final static Logger log = LogManager
            .getLogger(RedditGetMethod.class);

    private final String methodUrl = "https://oauth.reddit.com";

    @SuppressWarnings("boxing")
    public HttpResponse<String> getMethod(final String method,
            final Map<String, String> params)
            throws IOException, InterruptedException {
        String fullUrl;

        if (!params.isEmpty()) {
            final var form = params.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&"));

            log.debug("form: {}, {}", form, form.length());

            fullUrl = String.format("%s/%s?%s", this.methodUrl,
                    method, form);
        }
        else {
            fullUrl = String.format("%s/%s", this.methodUrl,
                    method);
        }

//        log.debug("fullUrl: " + fullUrl);

        final var redditOAuth = new RedditOAuth();

        // loads the OAuth token for Configuration.getOAuthToken().
        redditOAuth.getAuthToken();

        final var request = HttpRequest.newBuilder()
                .headers("User-Agent",
                        "java:com.objecteffects.reddit:v0.0.1 (by /u/lumpynose)")
                .header("Authorization",
                        "bearer " + Configuration.getOAuthToken())
                .GET()
                .uri(URI.create(fullUrl))
                .timeout(Duration.ofSeconds(15))
                .build();

//        log.debug("headers: {}", request.headers());

        final var client = RedditHttpClient.getHttpClient();

        final var response = client.send(request,
                BodyHandlers.ofString());

        log.debug("response status: {}",
                Integer.valueOf(response.statusCode()));
        log.debug("response headers: {}", response.headers());
        log.debug("response body: {}", response.body());

        return response;
    }

}
