package roomescape.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import roomescape.global.exception.business.ExternalApiException;
import roomescape.global.exception.business.RootBusinessException;
import roomescape.global.exception.business.impl.NotFoundException;
import roomescape.global.exception.business.impl.RelatedEntityExistException;
import roomescape.global.exception.security.impl.AuthenticationException;
import roomescape.global.exception.security.impl.AuthorizationException;
import roomescape.global.logging.util.Log;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(final NotFoundException e) {
        Log.exception(e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.business(e));
    }

    @ExceptionHandler(RelatedEntityExistException.class)
    public ResponseEntity<ErrorResponse> handleRelatedEntityExistException(final RelatedEntityExistException e) {
        Log.exception(e);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse.business(e));
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApiException(final ExternalApiException e) {
        Log.exception(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.externalApi(e));
    }

    @ExceptionHandler(RootBusinessException.class)
    public ResponseEntity<ErrorResponse> handleRootBusinessException(final RootBusinessException e) {
        Log.exception(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.business(e));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(final AuthenticationException e) {
        Log.exception(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.security(e));
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationException(final AuthorizationException e) {
        Log.exception(e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse.security(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception e) {
        Log.exception(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.unexpected(e));
    }
}
