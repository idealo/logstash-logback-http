package de.idealo.whitelabels.logback.library;

import feign.Client;

public class ClientFactory {

    public Client getClient(String clientName) {
        switch (clientName) {
            case "ApacheHttpClient":
                return new feign.httpclient.ApacheHttpClient();
            case "OkHttpClient":
                return new feign.okhttp.OkHttpClient();
            case "GoogleHttpClient":
                return new feign.googlehttpclient.GoogleHttpClient();
            default:
                String msg = String.format(
                    "Unsupported Client: '%s'. Please uses one of: [ApacheHttpClient, OkHttpClient, GoogleHttpClient]",
                    clientName
                );
                throw new IllegalArgumentException(msg);
        }
    }

}
