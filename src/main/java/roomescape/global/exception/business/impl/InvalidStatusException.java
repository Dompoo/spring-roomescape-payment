package roomescape.global.exception.business.impl;

import roomescape.global.exception.business.BusinessErrorCode;
import roomescape.global.exception.business.RootBusinessException;

public class InvalidStatusException extends RootBusinessException {

    public InvalidStatusException(BusinessErrorCode code) {
        super(code);
    }
}
