package roomescape.global.auth.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.global.auth.LoginRequired;
import roomescape.global.exception.security.impl.AuthenticationException;
import roomescape.service.helper.MemberHelper;

import static roomescape.global.exception.security.SecurityErrorCode.SESSION_NOT_EXIST;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private static final String SESSION_KEY = "id";

    private final MemberHelper memberHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        LoginRequired annotation = handlerMethod.getMethodAnnotation(LoginRequired.class);
        if (annotation == null) {
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SESSION_KEY) == null) {
            throw new AuthenticationException(SESSION_NOT_EXIST);
        }

        long memberId = (long) session.getAttribute(SESSION_KEY);
        if (!memberHelper.isExist(memberId)) {
            throw new AuthenticationException(SESSION_NOT_EXIST);
        }

        return true;
    }
}
