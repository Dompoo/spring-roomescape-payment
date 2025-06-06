package roomescape.global.auth.argument_resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.global.auth.LoginInfo;
import roomescape.global.exception.security.impl.AuthorizationException;

import static roomescape.global.exception.security.SecurityErrorCode.SESSION_NOT_EXIST;

@Component
public class LoginInfoArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(LoginInfo.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer, final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {
        final HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
        final HttpSession session = request.getSession();
        Object memberId = session.getAttribute("id");
        if (memberId == null) {
            throw new AuthorizationException(SESSION_NOT_EXIST);
        }
        return LoginInfo.fromObject(memberId);
    }
}
