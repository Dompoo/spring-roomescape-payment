package roomescape.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.global.auth.argument_resolver.LoginInfoArgumentResolver;
import roomescape.global.auth.interceptor.LoginInterceptor;
import roomescape.global.auth.interceptor.RoleInterceptor;
import roomescape.global.logging.httpLog.RequestLoggingInterceptor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RoleInterceptor roleInterceptor;
    private final LoginInterceptor loginInterceptor;
    private final RequestLoggingInterceptor requestLoggingInterceptor;
    private final LoginInfoArgumentResolver loginInfoArgumentResolver;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginInfoArgumentResolver);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/reservation").setViewName("reservation");
        registry.addViewController("/reservation-mine").setViewName("reservation-mine");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/signup").setViewName("signup");

        registry.addViewController("/admin").setViewName("admin/index");
        registry.addViewController("/admin/reservation").setViewName("admin/reservation-new");
        registry.addViewController("/admin/time").setViewName("admin/time");
        registry.addViewController("/admin/theme").setViewName("admin/theme");
        registry.addViewController("/admin/waiting").setViewName("admin/waiting");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
        registry.addInterceptor(roleInterceptor).addPathPatterns("/**");
        registry.addInterceptor(requestLoggingInterceptor).addPathPatterns("/**").excludePathPatterns("/favicon.ico");
    }
}
