# 1 Security Considerations

## 1.1 Lacking of Input Vaidation

### 1.1.1 Lacking of input validation for JSON request body for */api/spaces*

To solve the issue, you can add annotations on request DTO and don't forget to add `@Valid` annotation on the method parameter in controller.

### 1.1.2 Lacking of input validation for JPA entity.

To solve this issue, you can add JPA annotation on entity class. The judgement will firstly occure on the server side rather than database.

### 1.1.3 Lacking of full context input validation

To solve this issue, validate all inputs within the context. (e.g. `MessageController.get`)

## 1.2 Misconfiguration on default exception handling strategy

### 1.2.1 Exception details included in JSON or whitelabel error page

If we disable whitelabel error page by properties such as:

```properties
server.error.whitelabel.enabled=false
```

We may facing a circular issue for not defining an error page for container. So we can do it in another way to disable `ErrorMvcAutoConfiguration.class`

```java
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class WebAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAppApplication.class, args);
    }
}
```

But in this way the default JSON error message will also be affected while request is accepting *application/json*. So the easiest way is to define a restful controller mapping `/error` path.

```java
@RestController
public class FallbackErrorController implements ErrorController {
    @SuppressWarnings("unused")
    static class ErrorJson {
        private final int status;

        private ErrorJson(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }

    private static final String PATH = "/error";

    @RequestMapping(PATH)
    ErrorJson error(HttpServletResponse response) {
        return new ErrorJson(response.getStatus());
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
```

## 1.3 Lack of audit logs

### 1.3.1 The log is not persistent

To solve this issue, modify the configuration to send log to file or logging API. If you use log files, it is recommended to roll the file by fixed size (not fixed time).

### 1.3.2 The log is not structured

Add JSON encoder to gradle configuration.

```groovy
implementation 'net.logstash.logback:logstash-logback-encoder:6.6'
```

Add logback configuration for JSON output to file and human readable output to console (*src/main/resources/logback-spring.xml*):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property resource="application.properties" />
    <contextName>${spring.application.name}</contextName>
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${logging.default-path}/${spring.application.name}.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!-- rollover daily -->
            <fileNamePattern>${spring.application.name}-%d{yyyy-MM-dd}.%i.txt</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>60</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <contextName>
                    <fieldName>app</fieldName>
                </contextName>
                <timestamp>
                    <fieldName>ts</fieldName>
                    <timeZone>UTC</timeZone>
                </timestamp>
                <loggerName>
                    <fieldName>logger</fieldName>
                </loggerName>
                <logLevel>
                    <fieldName>level</fieldName>
                </logLevel>
                <callerData>
                    <classFieldName>class</classFieldName>
                    <methodFieldName>method</methodFieldName>
                    <lineFieldName>line</lineFieldName>
                    <fileFieldName>file</fileFieldName>
                </callerData>
                <threadName>
                    <fieldName>thread</fieldName>
                </threadName>
                <mdc />
                <arguments>
                    <includeNonStructuredArguments>false</includeNonStructuredArguments>
                </arguments>
                <stackTrace>
                    <fieldName>stack</fieldName>
                </stackTrace>
                <message>
                    <fieldName>msg</fieldName>
                </message>
            </providers>
        </encoder>
    </appender>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{dd-MM-yyyy HH:mm:ss.SSS} %magenta([%thread]) %highlight(%-5level) %logger{36}.%M - %msg%n</pattern>
        </encoder>
    </appender>
    <root>
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

And add logging configuration information in properties configuration:

```properties
# Spring

# This turns off the generic Spring Banner output which is NOT JSON friendly.
spring.main.banner-mode=OFF
# All Spring Applications should have this property available because it is used throughout the Spring Framework for outputting the application’s name in some of the logs
spring.application.name=poorApi

# Logging

logging.default-path=./logs
logging.level.root=INFO
logging.level.org.springframework=WARN
logging.level.org.hibernate=WARN
logging.level.org.hibernate.SQL=OFF
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=OFF
```

Using correct way to log in either user friendly manner and structural manner.

Please **DO NOT** use message formatting

```java
log.info("Processing company {} of {}", companyIndex, companyCount);
```

Because this will produce the following structure log:

```json
{ ... "msg":"Processing company 128 of 250" }
```

We should use structural arguments:

```java
import static net.logstash.logback.argument.StructuredArguments.v;

log.info(
    "Processing company",
    v("companyIndex", companyIndex), v("companyCount", companyCount));
```

Which produces the following log:

```json
{
  ... 
  "msg":"Processing company",
  "companyIndex":128,
  "companyCount":250
}
```