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
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.objecteffects.reddit.main.Configuration;

public class HttpClientRedditOAuth {
    private final static Logger log = LogManager
            .getLogger(HttpClientRedditOAuth.class);

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .version(Version.HTTP_2)
            .followRedirects(Redirect.NORMAL)
            .build();

    private final String authUrl = "https://www.reddit.com";
    private final String methodUrl = "https://oauth.reddit.com";

    public HttpResponse<String> getAuthToken()
            throws IOException, InterruptedException {
        final Map<String, String> params = new HashMap<>();

        params.put("grant_type", "password");
        params.put("username", Configuration.getUsername());
        params.put("password", Configuration.getPassword());

        final var username = Configuration.getClientId();
        final var password = Configuration.getSecret();

        log.debug("client_id: {}", username);
        log.debug("secret: {}", password);

        final var method = "api/v1/access_token";

        final var fullUrl = String.format("%s/%s", this.authUrl, method);

        log.debug("fullUrl: {}", fullUrl);

        final var form = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        log.debug("form: {}", form);

        final var request = HttpRequest.newBuilder()
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .headers("User-Agent",
                        "java:com.objecteffects.reddit:v0.0.1 (by /u/lumpynose)")
                .header("Authorization", basicAuth(username, password))
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .uri(URI.create(fullUrl))
                .timeout(Duration.ofSeconds(15))
                .build();

        log.debug("request headers: {}", request.headers());

        final var response = this.client.send(request, BodyHandlers.ofString());

        log.debug("response status: {}",
                Integer.valueOf(response.statusCode()));
        log.debug("response headers: {}", response.headers());
        log.debug("response body: {}", response.body());

        final Gson gson = new Gson();

        final TypeToken<Map<String, String>> mapType = new TypeToken<>() {
            // nothing here
        };

        final Map<String, String> stringMap = gson.fromJson(response.body(),
                mapType);

        if (!stringMap.containsKey("access_token")) {
            log.error("no access_token");

            throw new IllegalStateException("no access_token");
        }

        final var access_token = stringMap.get("access_token");

        log.debug("access_token: {}", access_token);

        Configuration.setOauthToken(access_token);

        return response;
    }

    public HttpResponse<String> revokeToken()
            throws IOException, InterruptedException {
        final Map<String, String> params = new HashMap<>();

        final var access_token = Configuration.getOauthToken();

        params.put("token", access_token);
        params.put("token_type_hint", "access_token");

        final var username = Configuration.getClientId();
        final var password = Configuration.getSecret();

        final var form = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        log.debug("form: {}", form);

        final var method = "api/v1/revoke_token";
        final var fullUrl = String.format("%s/%s", this.authUrl, method);

        log.debug("fullUrl: " + fullUrl);

        final var request = HttpRequest.newBuilder()
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .headers("User-Agent",
                        "java:com.objecteffects.reddit:v0.0.1 (by /u/lumpynose)")
                .header("Authorization", basicAuth(username, password))
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .uri(URI.create(fullUrl))
                .timeout(Duration.ofSeconds(15))
                .build();

        log.debug("headers: {}", request.headers());

        final var response = this.client.send(request, BodyHandlers.ofString());

        return response;
    }

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

        final var request = HttpRequest.newBuilder()
                .headers("User-Agent",
                        "java:com.objecteffects.reddit:v0.0.1 (by /u/lumpynose)")
                .header("Authorization",
                        "bearer " + Configuration.getOauthToken())
                .GET()
                .uri(URI.create(fullUrl))
                .timeout(Duration.ofSeconds(15))
                .build();

//        log.debug("headers: {}", request.headers());

        final var response = this.client.send(request,
                BodyHandlers.ofString());

        return response;
    }

    private static String basicAuth(final String username,
            final String password) {
        return "Basic " + Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes());
    }
}
