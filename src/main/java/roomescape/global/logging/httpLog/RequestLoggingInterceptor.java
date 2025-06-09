package roomescape.global.logging.httpLog;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import roomescape.global.logging.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final Object handler) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("Method", request.getMethod());
        logData.put("URI", request.getRequestURI());
        logData.put("Headers", Collections.list(request.getHeaderNames()).stream()
                .map(name -> name + ": " + request.getHeader(name))
                .collect(Collectors.joining(", ")));
        logData.put("Body", readRequestBodyAsString((ContentCachingRequestWrapper) request));

        Log.httpRequest(logData);
        return true;
    }

    private static String readRequestBodyAsString(ContentCachingRequestWrapper request) {
        return new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
    }

    @Override
    public void afterCompletion(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final Object handler, @Nullable final Exception ex) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("Request URI", request.getRequestURI());
        logData.put("Status", response.getStatus());
        logData.put("Headers", response.getHeaderNames().stream()
                .map(name -> name + ": " + response.getHeader(name))
                .collect(Collectors.joining(", ")));
        logData.put("Body", readResponseBodyAsString((ContentCachingResponseWrapper) response));

        Log.httpResponse(logData);
    }

    private static String readResponseBodyAsString(ContentCachingResponseWrapper response) {
        return new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
    }
}
