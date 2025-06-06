package roomescape.global.exception.security.impl;

import roomescape.global.exception.security.RootSecurityException;
import roomescape.global.exception.security.SecurityErrorCode;

public class AuthenticationException extends RootSecurityException {

    public AuthenticationException(final SecurityErrorCode code) {
        super(code);
    }
}
