package de.idealo.whitelabels.logback;

import static java.util.logging.Level.WARNING;

import java.io.IOException;
import java.util.logging.Logger;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class HttpServerHelper {

    public static MockWebServer startServer(String body, int code) {
        try {
            MockWebServer server = new MockWebServer();
            Logger.getLogger(server.getClass().getName()).setLevel(WARNING);

            server.enqueue(new MockResponse().setBody(body).setResponseCode(code));
            server.start();
            return server;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void stopServer(MockWebServer server) {
        try {
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
