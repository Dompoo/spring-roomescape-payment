package roomescape.global.exception.business.impl;

import roomescape.global.exception.business.BusinessErrorCode;
import roomescape.global.exception.business.RootBusinessException;

public class NotFoundException extends RootBusinessException {

    public NotFoundException(final BusinessErrorCode code) {
        super(code);
    }
}
