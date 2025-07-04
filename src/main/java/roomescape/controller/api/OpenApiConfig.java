package roomescape.controller.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.examples.Example;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import roomescape.global.exception.business.BusinessErrorCode;
import roomescape.global.exception.security.SecurityErrorCode;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

@OpenAPIDefinition(
        info = @Info(
                title = "방탈출 예약 및 결제 API",
                version = "v1.0.0"
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "로컬 개발 서버"),
                @Server(url = "http://3.34.186.106:8080", description = "개발 환경 서버"),
        }
)
@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("유저 API")
                .pathsToMatch("/api/**")
                .pathsToExclude("/api/admin/**")
                .addOpenApiCustomizer(exampleInjector())
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("어드민 API")
                .pathsToMatch("/api/admin/**")
                .addOpenApiCustomizer(exampleInjector())
                .build();
    }

    @Bean
    public OpenApiCustomizer exampleInjector() {
        return openApi -> {
            Components components = openApi.getComponents();

            Arrays.stream(BusinessErrorCode.values()).forEach(errorCode -> {
                Example example = new Example()
                        .value(Map.of(
                                "errorCode", errorCode.name(),
                                "message", errorCode.message(),
                                "timestamp", LocalDateTime.now()
                        ));
                components.addExamples(errorCode.name(), example);
            });

            Arrays.stream(SecurityErrorCode.values()).forEach(errorCode -> {
                Example example = new Example()
                        .value(Map.of(
                                "errorCode", errorCode.name(),
                                "message", errorCode.clientMessage(),
                                "timestamp", LocalDateTime.now()
                        ));
                components.addExamples(errorCode.name(), example);
            });
        };
    }
}
