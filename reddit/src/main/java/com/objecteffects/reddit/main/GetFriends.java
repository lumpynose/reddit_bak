package com.objecteffects.reddit.main;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.objecteffects.reddit.http.FriendAbout;
import com.objecteffects.reddit.http.Friends;
import com.objecteffects.reddit.http.Friends.Friend;
import com.objecteffects.reddit.http.RedditGetMethod;
import com.objecteffects.reddit.http.RedditOAuth;

public class GetFriends {
    private final static Logger log = LogManager
            .getLogger(GetFriends.class);
    final Gson gson = new Gson();

    @SuppressWarnings("boxing")
    public List<Friend> getFriends(final boolean getKarma)
            throws IOException, InterruptedException {
        final var client = new RedditGetMethod();

        final var methodResponse = client
                .getMethod("prefs/friends", Collections.emptyMap());

        if (methodResponse == null) {
            throw new IllegalStateException("null friends respones");
        }

        log.debug("friends method response status: {}",
                Integer.valueOf(methodResponse.statusCode()));

        final TypeToken<List<Friends>> jaType = new TypeToken<>() {
            // nothing here
        };

        final List<Friends> data = this.gson.fromJson(methodResponse.body(),
                jaType);

        final List<Friend> friends = data.get(0).getData().getFriendsList();

        log.debug("friends length: {}", friends.size());

        if (getKarma) {
            decodeAbout(friends);
        }

        RedditOAuth.revokeToken();

        return friends;
    }

    private List<Friend> decodeAbout(final List<Friend> friends)
            throws IOException, InterruptedException {
        final var client = new RedditGetMethod();

        for (final Friend f : friends) {
            final var aboutMethod = String.format("user/%s/about", f.getName());

            final var aboutMethodResponse = client
                    .getMethod(aboutMethod, Collections.emptyMap());

            if (aboutMethodResponse == null) {
                f.setKarma(0);
            }
            else {
                final FriendAbout fabout = this.gson.fromJson(
                        aboutMethodResponse.body(), FriendAbout.class);

                if (fabout.getData() == null) {
                    log.debug("{}: no about data", f.getName());

                    f.setKarma(0);
                }
                else {
                    f.setKarma(fabout.getData().getTotalKarma());
                }
            }

            Thread.sleep(1005);
        }

        return friends;
    }
}
