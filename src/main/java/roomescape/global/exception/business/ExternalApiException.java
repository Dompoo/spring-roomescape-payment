package roomescape.global.exception.business;

public class ExternalApiException extends RuntimeException {

    private final String code;

    public ExternalApiException(BusinessErrorCode errorCode) {
        super(errorCode.message());
        this.code = errorCode.name();
    }

    public ExternalApiException(BusinessErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.name();
    }

    public String codeName() {
        return code;
    }
}
