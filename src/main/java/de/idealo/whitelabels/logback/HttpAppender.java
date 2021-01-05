package de.idealo.whitelabels.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;


public class HttpAppender extends AppenderBase<ILoggingEvent> {

    private static final Header HEADER = new BasicHeader("content-type", "application/json");

    private Encoder<ILoggingEvent> encoder;
    private String destination;
    private URI uri;

    @Override
    public synchronized void start() {
        if (isStarted()) {
            return;
        }
        verifyConfigurationParameters();
        uri = URI.create(destination);
        addInfo("HttpAppender started: '" + destination + "'");

        super.start();
    }

    @Override
    public void append(ILoggingEvent event) {
        if (!isStarted()) {
            return;
        }
        String json = composeJsonFromEvent(event);
        putJson(json);
    }

    private void putJson(String json) {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPut request = new HttpPut(uri);
            request.addHeader(HEADER);
            request.setEntity(new StringEntity(json));
            CloseableHttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                addError("PUT request Error: "
                    + "  url: '" + destination + "'"
                    + ", statusCode: '" + response.getStatusLine().getStatusCode() + "'"
                    + ", message: '" + response.getStatusLine().getReasonPhrase() + "'"
                    + ", json: '" + json + "'"
                );
            }
        } catch (Exception ex) {
            addError("Can't execute PUT request to: '" + destination + "' JSON:\n" + json, ex);
        }
    }

    private String composeJsonFromEvent(ILoggingEvent event) {
        return new String(encoder.encode(event), StandardCharsets.UTF_8);
    }

    private void verifyConfigurationParameters() {
        if (encoder == null) {
            addError("No encoder was configured. Use <encoder> to specify the class name");
        }
        if (destination == null) {
            addError("No destination was configured. Use <destination> to specify the URL");
        }
    }

    public void setEncoder(Encoder<ILoggingEvent> encoder) {
        this.encoder = encoder;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

}
