package com.objecteffects.reddit.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.objecteffects.reddit.main.Configuration;

public class RedditHttpClient {
    private final static Logger log = LogManager
            .getLogger(RedditHttpClient.class);

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(RedditHttpClient.timeoutSeconds))
            .version(Version.HTTP_2)
            .followRedirects(Redirect.NORMAL)
            .build();

    final static String authUrl = "https://www.reddit.com";
    final static String methodUrl = "https://oauth.reddit.com";
    final static int timeoutSeconds = 15;

    @SuppressWarnings("boxing")
    final static List<Integer> okCodes = Arrays.asList(200, 201, 202, 203, 204);

    public static HttpClient getHttpClient() {
        return client;
    }

    @SuppressWarnings("boxing")
    public static HttpResponse<String> clientSend(
            final HttpRequest.Builder request,
            final String method,
            final Map<String, String> params)
            throws InterruptedException, IOException {
        final String fullUrl;

        if (!params.isEmpty()) {
            final String form = params.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&"));

            log.debug("form: {}, {}", form, form.length());

            fullUrl = String.format("%s/%s?%s", RedditHttpClient.methodUrl,
                    method, form);
        }
        else {
            fullUrl = String.format("%s/%s", RedditHttpClient.methodUrl,
                    method);
        }

        log.debug("fullUrl: {}", fullUrl);

        // loads the OAuth token for Configuration.getOAuthToken().
        RedditOAuth.getAuthToken();

        final HttpRequest buildRequest = request
                .headers("User-Agent",
                        "java:com.objecteffects.reddit:v0.0.1 (by /u/lumpynose)")
                .header("Authorization",
                        "bearer " + Configuration.getOAuthToken())
                .uri(URI.create(fullUrl))
                .timeout(Duration.ofSeconds(RedditHttpClient.timeoutSeconds))
                .build();

//        log.debug("headers: {}", request.headers());

        HttpResponse<String> response = null;

        try {
            log.debug("method: {}", method);

            response = client.send(buildRequest, BodyHandlers.ofString());

            log.debug("response status: {}",
                    Integer.valueOf(response.statusCode()));
            log.debug("response headers: {}", response.headers());
            log.debug("response body: {}", response.body());
        }
        catch (IOException | InterruptedException e) {
            log.debug("exception: {}", e);

            // fall through to retries below

            response = null;
        }

        if (response == null || !okCodes.contains(response.statusCode())) {
            for (int i = 1; i < 11; i++) {
                Thread.sleep(i * 1500);

                try {
                    log.debug("method: {}", method);

                    response = client.send(buildRequest,
                            BodyHandlers.ofString());

                    log.debug("response status: {}",
                            Integer.valueOf(response.statusCode()));
                    log.debug("response headers: {}", response.headers());
                    log.debug("response body: {}", response.body());

                    if (okCodes.contains(response.statusCode())) {
                        break;
                    }
                }
                catch (IOException | InterruptedException e) {
                    log.debug("exception: {}", e);

                    // keep retrying

                    response = null;
                }
            }
        }

        if (response == null || !okCodes.contains(response.statusCode())) {
            return null;
        }

        return response;
    }
}
