package roomescape.global.exception.security.impl;

import roomescape.global.exception.security.RootSecurityException;
import roomescape.global.exception.security.SecurityErrorCode;

public class AuthorizationException extends RootSecurityException {

    public AuthorizationException(final SecurityErrorCode code) {
        super(code);
    }
}
