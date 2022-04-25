package de.idealo.whitelabels.logback;

import static ch.qos.logback.classic.Level.INFO;
import static ch.qos.logback.classic.Level.WARN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import net.logstash.logback.encoder.LogstashEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HttpAppenderTest {

    private HttpAppender httpAppender;

    @Mock
    private LogstashEncoder encoder;

    @Mock
    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpAppender = new HttpAppender();
        httpAppender.setEncoder(encoder);
        httpAppender.setHttpClient(httpClient);
        httpAppender.setContext(new LoggerContext());
    }

    @Test
    void should_be_started() {
        httpAppender.start();
        assertThat(httpAppender.isStarted()).isTrue();
    }

    @Test
    void should_be_not_started() {
        assertThat(httpAppender.isStarted()).isFalse();
    }

    @Test
    void should_invoke_httpClient_if_appender_started() {
        httpAppender.start();
        LoggingEvent event = createEvent(INFO, "info");
        when(encoder.encode(any())).thenReturn(event.getMessage().getBytes());

        httpAppender.append(event);

        verify(httpClient).request(event.getMessage());
    }

    @Test
    void should_not_invoke_httpClient_if_appender_not_started() {
        LoggingEvent event = createEvent(WARN, "warn");

        httpAppender.append(event);

        verify(httpClient, times(0)).request(event.getMessage());
    }

    private LoggingEvent createEvent(Level level, String message) {
        LoggingEvent event = new LoggingEvent();
        event.setMessage(message);
        event.setLevel(level);
        return event;
    }

}
