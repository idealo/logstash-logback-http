# whitelabels-logstash-logback

logstash-logback-http provide *HttpAppender* to sending log messages from _logback_ to _logstash_ via *http* or *https*

## Building
``` shell
mvn clean install
```

## Using
pom.xml
```xml
<dependency>
  <groupId>de.idealo.whitelabels</groupId>
  <artifactId>logstash-logback-http</artifactId>
  <version>1.0.23</version>
</dependency>
```

## Configuring HttpAppender
resources/logback-spring.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
  <shutdownHook class="ch.qos.logback.core.hook.DelayingShutdownHook"/>

  <springProperty scope="context" name="projectName" source="whitelabels.project-name"
    defaultValue="whitelabels-indexation"/>
  <springProperty scope="context" name="stage" source="stage" defaultValue="local"/>

  <appender name="LOCAL-CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{YYYY-MM-dd HH:mm:ss.SSS} %5level %logger{0} [%t] [%mdc] - %msg%n</pattern>
    </encoder>
  </appender>

  <appender name="LOGSTASH"
    class="net.logstash.logback.appender.LoggingEventAsyncDisruptorAppender">
    <ringBufferSize>8192</ringBufferSize>
    <appender class="de.idealo.whitelabels.logback.HttpAppender">
      <httpClient class="de.idealo.whitelabels.logback.HttpClient">
        <destination>https://JOUR.LOGSTASH.URL</destination>
      </httpClient>
      <encoder class="net.logstash.logback.encoder.LogstashEncoder">
        <fieldNames>
          <timestamp>timestamp</timestamp>
        </fieldNames>
      </encoder>
    </appender>
  </appender>

  <springProfile name="local">
    <root level="info">
      <appender-ref ref="LOGSTASH"/>
      <appender-ref ref="LOCAL-CONSOLE"/>
    </root>
  </springProfile>

</configuration>
```

### Documentation:
* Logstash-Logback Encoder: https://github.com/logstash/logstash-logback-encoder
* Logback: http://logback.qos.ch/
* Logstash: https://www.elastic.co/logstash
