package de.idealo.whitelabels.logback;

import static java.net.HttpURLConnection.HTTP_OK;
import static net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils.isBlank;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.status.Status;
import de.idealo.whitelabels.logback.library.ClientFactory;
import de.idealo.whitelabels.logback.library.Logstash;
import feign.Client;
import feign.Feign;
import feign.Response;
import java.io.IOException;
import java.util.List;

public class HttpClient extends ContextAwareBase implements LifeCycle {

    private String destination;
    private Logstash logstash;
    private volatile boolean isStarted = false;
    private String client = "ApacheHttpClient";
    private final ClientFactory clientFactory = new ClientFactory();

    @Override
    public synchronized void start() {
        if (isStarted) {
            return;
        }
        if (isBlank(destination)) {
            addError("No encoder was configured. Use <destination> URL");
        }

        Client httpClient = clientFactory.getClient(client);

        if (isAnyErrors()) {
            return;
        }

        logstash = Feign.builder()
            .client(httpClient)
            .decoder((response, type) -> response)
            .target(Logstash.class, destination);

        isStarted = true;
        addInfo(String.format("HttpClient started: '%s'", destination));
    }

    @Override
    public synchronized void stop() {
        if (isStarted) {
            isStarted = false;
        }
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }

    public void put(String json) {
        if (!isStarted()) {
            return;
        }
        try {
            verifyResponse(logstash.put(json));
        } catch (Exception ex) {
            ioError(ex);
        }
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<Status> getStatusList() {
        return getStatusManager().getCopyOfStatusList();
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void verifyResponse(Response response) {
        if (response.status() != HTTP_OK && !"OK".equals(response.reason())) {
            String msg = String.format("ResponseCode: %s; Reason: %s; URL: %s",
                response.status(),
                response.reason(),
                response.request().url()
            );
            ioError(new IOException(msg));
        }
    }

    public void ioError(Exception ex) {
        addError(String.format("Can't execute PUT request to: '%s'", destination), ex);
    }

    private boolean isAnyErrors() {
        return getStatusManager().getCopyOfStatusList().stream().anyMatch(x -> x.getLevel() > 1);
    }

    @Override
    public String toString() {
        return destination == null ? client : client + " --> " + destination;
    }

}
