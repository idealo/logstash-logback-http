package de.idealo.whitelabels.logback.endpoints;

import feign.RequestLine;
import feign.Response;

public interface Logstash {

    @RequestLine("PUT /")
    Response put(String json);

    @RequestLine("POST /")
    Response post(String json);

}
