package roomescape.global.logging.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Collectors;

import static roomescape.global.logging.util.LogData.*;

public class Log {

    public static void httpRequest(HttpServletRequest request) {
        new MyLogger(LogLevel.INFO, LogType.HTTP_REQUEST)
                .add(METHOD, request.getMethod())
                .add(URI, request.getRequestURI())
                .add(HEADERS, readRequestHeadersAsString(request))
                .add(BODY, readRequestBodyAsString(request))
                .write();
    }

    private static String readRequestHeadersAsString(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames()).stream()
                .map(name -> name + ": " + request.getHeader(name))
                .collect(Collectors.joining(", "));
    }

    private static String readRequestBodyAsString(HttpServletRequest request) {
        return new String(((ContentCachingRequestWrapper) request).getContentAsByteArray(), StandardCharsets.UTF_8);
    }

    public static void httpResponse(HttpServletRequest request, HttpServletResponse response) {
        new MyLogger(LogLevel.INFO, LogType.HTTP_RESPONSE)
                .add(REQUEST_URI, request.getRequestURI())
                .add(STATUS, response.getStatus())
                .add(HEADERS, readRequestHeadersAsString(response))
                .add(BODY, readResponseBodyAsString(response))
                .write();
    }

    private static String readRequestHeadersAsString(HttpServletResponse response) {
        return response.getHeaderNames().stream()
                .map(name -> name + ": " + response.getHeader(name))
                .collect(Collectors.joining(", "));
    }

    private static String readResponseBodyAsString(HttpServletResponse response) {
        return new String(((ContentCachingResponseWrapper) response).getContentAsByteArray(), StandardCharsets.UTF_8);
    }

    public static void exception(Exception e) {
        new MyLogger(LogLevel.WARN, LogType.EXCEPTION)
                .add(EXCEPTION_NAME, e.getClass().getSimpleName())
                .add(EXCEPTION_MESSAGE, e.getMessage())
                .write();
    }

    public static void business(String event, String stage, String message, Class<?> location) {
        new MyLogger(LogLevel.INFO, LogType.BUSINESS)
                .add(EVENT, event)
                .add(STAGE, stage)
                .add(MESSAGE, message)
                .add(LOCATION, location.getSimpleName())
                .write();
    }

    @Slf4j
    public static class MyLogger {
        private final LogLevel level;

        public MyLogger(LogLevel level, LogType type) {
            this.level = level;
            add(LOG_TYPE, type);
        }

        public MyLogger add(LogData dataType, Object value) {
            MDC.put(dataType.name(), value.toString());
            return this;
        }

        public void write() {
            switch (level) {
                case DEBUG -> log.debug("");
                case INFO -> log.info("");
                case WARN -> log.warn("");
                case ERROR -> log.error("");
            }
        }
    }
}
