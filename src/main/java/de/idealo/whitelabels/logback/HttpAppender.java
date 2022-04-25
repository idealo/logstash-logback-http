package de.idealo.whitelabels.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.nio.charset.StandardCharsets;
import net.logstash.logback.encoder.LogstashEncoder;

public class HttpAppender extends AppenderBase<ILoggingEvent> {

    private LogstashEncoder encoder;
    private HttpClient httpClient;

    @Override
    public synchronized void start() {
        if (isStarted()) {
            return;
        }
        verifyConfigurationParameters();
        addInfo("HttpAppender started");

        super.start();
    }

    @Override
    public void append(ILoggingEvent event) {
        if (!isStarted()) {
            return;
        }
        String json = new String(encoder.encode(event), StandardCharsets.UTF_8);
        httpClient.request(json);
    }

    public void setEncoder(LogstashEncoder encoder) {
        this.encoder = encoder;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private void verifyConfigurationParameters() {
        if (encoder == null) {
            addError("No encoder was configured. Use <encoder> to specify the class name");
        }
        if (httpClient == null) {
            addError("No destination was configured. Use <httpClient> to specify HttpClient");
        }
    }

}
