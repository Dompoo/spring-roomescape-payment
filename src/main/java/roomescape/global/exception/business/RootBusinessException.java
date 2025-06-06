package roomescape.global.exception.business;

public abstract class RootBusinessException extends RuntimeException {

    private final BusinessErrorCode code;

    protected RootBusinessException(BusinessErrorCode code) {
        super(code.message());
        this.code = code;
    }

    protected RootBusinessException(BusinessErrorCode code, Object... args) {
        super(code.message().formatted(args));
        this.code = code;
    }

    public String codeName() {
        return code.name();
    }

    public String message() {
        return code.message();
    }
}
