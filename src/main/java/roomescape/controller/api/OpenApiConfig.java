package roomescape.controller.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("어드민 API")
                .pathsToMatch("/api/admin/**")
                .build();
    }
}
