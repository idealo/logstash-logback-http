package de.idealo.whitelabels.logback;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.Status;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpClientTest {

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = new HttpClient();
        httpClient.setContext(new LoggerContext());
    }

    @Test
    void should_be_not_started() {
        assertThat(httpClient.isStarted()).isFalse();
    }

    @Test
    void should_be_started() {
        httpClient.setDestination("http://localhost");
        httpClient.start();
        assertThat(httpClient.isStarted()).isTrue();
    }

    @Test
    void should_be_return_error_if_destination_is_null() {
        httpClient.start();
        List<Status> statuses = httpClient.getStatusManager().getCopyOfStatusList();
        assertThat(statuses.get(0)).isInstanceOf(ErrorStatus.class);
        assertThat(httpClient.isStarted()).isFalse();
    }
}