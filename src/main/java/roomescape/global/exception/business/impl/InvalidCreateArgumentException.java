package roomescape.global.exception.business.impl;

import roomescape.global.exception.business.BusinessErrorCode;
import roomescape.global.exception.business.RootBusinessException;

public class InvalidCreateArgumentException extends RootBusinessException {

    public InvalidCreateArgumentException(final BusinessErrorCode code) {
        super(code);
    }

    public InvalidCreateArgumentException(final BusinessErrorCode code, final Object... args) {
        super(code, args);
    }
}
