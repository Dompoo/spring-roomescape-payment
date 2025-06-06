package roomescape.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.global.exception.business.ExternalApiException;
import roomescape.global.exception.business.RootBusinessException;
import roomescape.global.exception.security.RootSecurityException;

import java.time.LocalDateTime;

public record ErrorResponse(
        @Schema(example = "예외 코드")
        String errorCode,
        @Schema(example = "예외 메시지")
        String message,
        @Schema(example = "2025-06-05T05:37:01")
        LocalDateTime timestamp
) {
    public static ErrorResponse business(RootBusinessException e) {
        return new ErrorResponse(
                e.codeName(),
                e.message(),
                LocalDateTime.now()
        );
    }

    public static ErrorResponse security(RootSecurityException e) {
        return new ErrorResponse(
                e.codeName(),
                e.clientMessage(),
                LocalDateTime.now()
        );
    }

    public static ErrorResponse externalApi(ExternalApiException e) {
        return new ErrorResponse(
                e.codeName(),
                e.getMessage(),
                LocalDateTime.now()
        );
    }

    public static ErrorResponse unexpected(Exception e) {
        return new ErrorResponse(
                e.getClass().getSimpleName(),
                e.getMessage(),
                LocalDateTime.now()
        );
    }
}
