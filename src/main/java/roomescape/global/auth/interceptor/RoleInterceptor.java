package roomescape.global.auth.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.domain.member.Member;
import roomescape.global.auth.RoleRequired;
import roomescape.global.exception.security.impl.AuthenticationException;
import roomescape.global.exception.security.impl.AuthorizationException;
import roomescape.service.helper.MemberHelper;

import java.util.Arrays;

import static roomescape.global.exception.security.SecurityErrorCode.AUTHORITY_LACK;
import static roomescape.global.exception.security.SecurityErrorCode.SESSION_NOT_EXIST;

@Component
@RequiredArgsConstructor
public class RoleInterceptor implements HandlerInterceptor {

    private static final String SESSION_KEY = "id";

    private final MemberHelper memberHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RoleRequired annotation = handlerMethod.getMethodAnnotation(RoleRequired.class);
        if (annotation == null) {
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SESSION_KEY) == null) {
            throw new AuthenticationException(SESSION_NOT_EXIST);
        }

        long memberId = (long) session.getAttribute(SESSION_KEY);
        Member member = memberHelper.getById(memberId);
        if (!hasAuthority(annotation, member)) {
            throw new AuthorizationException(AUTHORITY_LACK);
        }

        return true;
    }

    private static boolean hasAuthority(RoleRequired annotation, Member member) {
        return Arrays.asList(annotation.value()).contains(member.getRole());
    }
}
