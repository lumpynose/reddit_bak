package com.objecteffects.reddit.http;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.time.Duration;

public class RedditHttpClient {
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .version(Version.HTTP_2)
            .followRedirects(Redirect.NORMAL)
            .build();

    public static HttpClient getHttpClient() {
        return client;
    }
}
