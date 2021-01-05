package de.idealo.whitelabels.logback.library;

import feign.RequestLine;
import feign.Response;

public interface Logstash {

    @RequestLine("PUT /")
    Response put(String json);

}
