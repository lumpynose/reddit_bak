package com.objecteffects.reddit.http;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class RedditPutMethod {
    private final static Logger log = LogManager
            .getLogger(RedditPutMethod.class);

    public HttpResponse<String> putMethod(final String method,
            final Map<String, String> params)
            throws InterruptedException, IOException {

        final Gson gson = new Gson();

        final String json = gson.toJson(params);

        log.debug("json: {}", json);

        final HttpRequest.Builder deleteBuilder = HttpRequest.newBuilder()
                .PUT(BodyPublishers.ofString(json));

        return RedditHttpClient.clientSend(deleteBuilder, method,
                Collections.emptyMap());
    }
}
