package roomescape.global.logging.httpLog;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.global.logging.util.Log;

@Component
@RequiredArgsConstructor
public class HttpLoggingInterceptor implements HandlerInterceptor {

    private static final String[] STATIC_RESOURCE_EXTENSIONS = {
            ".html", ".css", ".js", ".png", ".favicon"
    };

    private static final String[] STATIC_RESOURCE_PREFIXES = {
            "/static/", "/templates/"
    };

    @Override
    public boolean preHandle(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final Object handler) {
        if (!isStaticResource(request)) Log.httpRequest(request);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final Object handler, @Nullable final Exception ex) {
        if (!isStaticResource(request)) Log.httpResponse(request, response);
    }

    private boolean isStaticResource(HttpServletRequest request) {
        String uri = request.getRequestURI().toLowerCase();

        for (String ext : STATIC_RESOURCE_EXTENSIONS) {
            if (uri.endsWith(ext)) {
                return true;
            }
        }

        for (String prefix : STATIC_RESOURCE_PREFIXES) {
            if (uri.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }
}
