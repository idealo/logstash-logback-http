package de.idealo.whitelabels.logback;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.status.Status;
import de.idealo.whitelabels.logback.endpoints.Logstash;
import feign.Feign;
import feign.Logger.Level;
import feign.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

public class HttpClient extends ContextAwareBase implements LifeCycle {

    private String destination;
    private Logstash logstash;
    private volatile boolean isStarted = false;

    @Override
    public synchronized void start() {
        if (isStarted) {
            return;
        }
        if (destination == null || destination.isEmpty()) {
            addError("No encoder was configured. Use <destination> URL");
        }

        if (hasAnyErrors()) {
            return;
        }

        logstash = Feign.builder()
            .decoder((response, type) -> response)
            .logLevel(Level.NONE)
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
            addWarn(String.format("Can't execute POSTtry post request. URL: '%s'", destination), ex);
        }
    }

    @Override
    public String toString() {
        return destination == null ? "HttpClient" : "HttpClient --> " + destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<Status> getStatusList() {
        return getStatusManager().getCopyOfStatusList();
    }

    private void verifyResponse(Response response) throws IOException {
        if (response.status() != HttpURLConnection.HTTP_OK) {
            String msg = String.format("ResponseCode: %s; Reason: %s; URL: %s",
                response.status(),
                response.reason(),
                response.request().url()
            );
            throw new IOException(msg);
        }
    }

    private boolean hasAnyErrors() {
        return getStatusList().stream().anyMatch(x -> x.getLevel() > 1);
    }

}
