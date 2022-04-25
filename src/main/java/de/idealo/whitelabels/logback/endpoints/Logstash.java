package de.idealo.whitelabels.logback.endpoints;

import feign.RequestLine;
import feign.Response;

public interface Logstash {

    @RequestLine("POST /")
    Response request(String json);

}
