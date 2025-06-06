package roomescape.global.exception.business;

public class ExternalApiException extends RuntimeException {

    private final String errorCode;

    public ExternalApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String codeName() {
        return errorCode;
    }
}
