package com.objecteffects.reddit.http;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class TestGetMethodFriends {
    private final static Logger log = LogManager
            .getLogger(TestGetMethodFriends.class);

    @Test
    public void testGetMethod() throws IOException, InterruptedException {
        final var client = new HttpClientRedditOAuth();

        final var authResponse = client.getAuthToken();

        log.debug("auth response status: {}",
                Integer.valueOf(authResponse.statusCode()));
        log.debug("auth response headers: {}", authResponse.headers());
        log.debug("auth response body: {}", authResponse.body());

        final var params = Map.of("limit", "5");

        final var methodResponse = client
                .getMethod("prefs/friends", params);

        log.debug("method response status: {}",
                Integer.valueOf(methodResponse.statusCode()));
//        log.debug("method response headers: {}", methodResponse.headers());
//        log.debug("method response body: {}", methodResponse.body());

        decodeBody(methodResponse.body(), client);

        final var revokeResponse = client.revokeToken();

        log.debug("revoke response status: {}",
                Integer.valueOf(revokeResponse.statusCode()));
        log.debug("revoke response headers: {}", revokeResponse.headers());
        log.debug("revoke response body: {}", revokeResponse.body());
    }

    @SuppressWarnings("boxing")
    private void decodeBody(final String body,
            final HttpClientRedditOAuth client)
            throws IOException, InterruptedException {
        final var gson = new Gson();

        final TypeToken<List<Friends>> jaType = new TypeToken<>() {
            // nothing here
        };

        final List<Friends> data = gson.fromJson(body, jaType);

        log.debug("data length: {}",
                data.get(0).getData().getFriendsList().size());

        testBanned(client, gson);

        var i = 0;

        for (final var f : data.get(0).getData().getFriendsList()) {
            if (i++ >= 0) {
                break;
            }

//            log.debug("{}", f.getName());

            final var aboutMethod = String.format("user/%s/about",
                    f.getName());

            final var aboutMethodResponse = client
                    .getMethod(aboutMethod, Collections.emptyMap());

//            log.debug("about response body: {}", aboutMethodResponse.body());

            final var fabout = gson.fromJson(aboutMethodResponse.body(),
                    FriendAbout.class);

            if (fabout.getData() == null) {
                log.debug("{}: no about data", f.getName());

                f.setKarma(0);
            }
            else {
                f.setKarma(fabout.getData().getTotalKarma());
            }

            final var overviewMethod = String.format("user/%s/overview",
                    f.getName());

            final var overviewMethodResponse = client
                    .getMethod(overviewMethod, Collections.emptyMap());

            log.debug("overview response body: {}",
                    overviewMethodResponse.body());

            log.debug("{}, total karma: {}", f.getName(), f.getKarma());

//             Thread.sleep(1000);

//            extracted(data);
        }
    }

    private void testBanned(final HttpClientRedditOAuth client,
            final Gson gson) throws IOException, InterruptedException {
        final var userNmae = "ECUlightBBC";

        final var aboutMethod = String.format("user/%s/about",
                userNmae);

        final var aboutResponse = client
                .getMethod(aboutMethod, Collections.emptyMap());

        log.debug("about response status: {}",
                aboutResponse.statusCode());
        log.debug("about response body: {}", aboutResponse.body());

        final var fabout = gson.fromJson(aboutResponse.body(),
                FriendAbout.class);

        if (fabout.getData() == null) {
            log.debug("{}: no about data", userNmae);

            return;
        }

        log.debug("isSuspended: {}", fabout.getData().getIsSuspended());

        final var overviewMethod = String.format("user/%s/overview",
                userNmae);

        final var overviewResponse = client
                .getMethod(overviewMethod, Collections.emptyMap());

        log.debug("overview response status: {}",
                overviewResponse.statusCode());
        log.debug("overview response body: {}",
                overviewResponse.body());

    }

    @SuppressWarnings("unused")
    private void extracted(final List<Friends> data) throws IOException {
        Collections.sort(data.get(0).getData().getFriendsList(),
                Collections.reverseOrder());

        final var fileName = "d:/tmp/friends.txt";

        try (var writer = new PrintWriter(
                new FileWriter(fileName, false))) {
            for (final var f : data.get(0).getData().getFriendsList()) {

                log.debug("{}, {}", f.getName(), f.getKarma());

                final var line = String.format(
                        "<a href='https://www.reddit.com/user/%s/submitted/?sort=new' target='_blank'>%s</a>, %d<br />%n",
                        f.getName(), f.getName(), f.getKarma());
                writer.append(line);
            }
        }
    }
}
