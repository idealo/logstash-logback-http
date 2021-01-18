package de.idealo.whitelabels.logback.library;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import feign.Client;
import feign.googlehttpclient.GoogleHttpClient;
import feign.httpclient.ApacheHttpClient;
import feign.okhttp.OkHttpClient;
import org.junit.jupiter.api.Test;

class ClientFactoryTest {

    private final ClientFactory clientFactory = new ClientFactory();

    @Test
    void should_retuen_ApacheHttpClient() {
        Client client = clientFactory.getClient("ApacheHttpClient");
        assertThat(client).isInstanceOf(ApacheHttpClient.class);
    }

    @Test
    void should_retuen_OkHttpClient() {
        Client client = clientFactory.getClient("OkHttpClient");
        assertThat(client).isInstanceOf(OkHttpClient.class);
    }

    @Test
    void should_retuen_GoogleHttpClient() {
        Client client = clientFactory.getClient("GoogleHttpClient");
        assertThat(client).isInstanceOf(GoogleHttpClient.class);
    }

    @Test
    void should_retuen_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            clientFactory.getClient("WrongHttpClientName");
        });
    }

}