package de.idealo.whitelabels.logback;

import static de.idealo.whitelabels.logback.HttpServerHelper.startServer;
import static de.idealo.whitelabels.logback.HttpServerHelper.stopServer;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.Status;
import java.util.List;
import java.util.stream.Stream;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class HttpClientsIntegrationTest {

    private static Stream<HttpClient> provideHttpClients() {
        HttpClient apacheHttpClient = new HttpClient();
        apacheHttpClient.setContext(new LoggerContext());
        apacheHttpClient.setClient("ApacheHttpClient");

        HttpClient okHttpClient = new HttpClient();
        okHttpClient.setContext(new LoggerContext());
        okHttpClient.setClient("OkHttpClient");

        HttpClient googleHttpClient = new HttpClient();
        googleHttpClient.setContext(new LoggerContext());
        googleHttpClient.setClient("GoogleHttpClient");

        HttpClient apacheHttpClientDefault = new HttpClient();
        apacheHttpClientDefault.setContext(new LoggerContext());

        return Stream.of(googleHttpClient, apacheHttpClient, okHttpClient, apacheHttpClientDefault);
    }

    @ParameterizedTest
    @MethodSource("provideHttpClients")
    void should_have_status_OK_when_server_returns_OK(HttpClient httpClient) {
        MockWebServer server = startServer("OK", HTTP_OK);
        httpClient.setDestination(server.url("/").toString());
        httpClient.start();

        httpClient.put("{}");

        stopServer(server);
        httpClient.stop();

        List<Status> statuses = httpClient.getStatusList();

        assertThat(statuses.size()).isEqualTo(1);
        assertThat(statuses.get(0)).isInstanceOf(InfoStatus.class);
    }


    @ParameterizedTest
    @MethodSource("provideHttpClients")
    void should_have_status_that_contains_error_when_server_returns_error(HttpClient httpClient) {
        MockWebServer server = startServer("error", HTTP_INTERNAL_ERROR);
        httpClient.setDestination(server.url("/").toString());
        httpClient.start();

        httpClient.put("{}");

        stopServer(server);
        httpClient.stop();

        List<Status> statuses = httpClient.getStatusList();

        assertThat(statuses.size()).isEqualTo(2);
        assertThat(statuses.get(0).getThrowable()).isNull();
        assertThat(statuses.get(1).getThrowable()).isNotNull();
        assertThat(statuses.get(0)).isInstanceOf(InfoStatus.class);
        assertThat(statuses.get(1)).isInstanceOf(ErrorStatus.class);
        assertThat(statuses.get(1).getMessage())
            .contains("Can't execute PUT request to:");
    }

    @ParameterizedTest
    @MethodSource("provideHttpClients")
    void should_have_status_that_contains_error_when_request_to_bad_url(HttpClient httpClient) {
        httpClient.setDestination("http://bad_url");
        httpClient.start();

        httpClient.put("{}");

        List<Status> statuses = httpClient.getStatusList();

        assertThat(statuses.size()).isEqualTo(2);
        assertThat(statuses.get(0).getThrowable()).isNull();
        assertThat(statuses.get(1).getThrowable()).isNotNull();
        assertThat(statuses.get(0)).isInstanceOf(InfoStatus.class);
        assertThat(statuses.get(1)).isInstanceOf(ErrorStatus.class);
        assertThat(statuses.get(1).getMessage())
            .contains("Can't execute PUT request to: 'http://bad_url'");
    }

}