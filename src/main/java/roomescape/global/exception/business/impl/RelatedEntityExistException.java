package roomescape.global.exception.business.impl;

import roomescape.global.exception.business.BusinessErrorCode;
import roomescape.global.exception.business.RootBusinessException;

public class RelatedEntityExistException extends RootBusinessException {

    public RelatedEntityExistException(final BusinessErrorCode code) {
        super(code);
    }
}
