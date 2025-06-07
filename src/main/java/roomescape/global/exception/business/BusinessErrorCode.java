package roomescape.global.exception.business;

public enum BusinessErrorCode {
    // 테마
    THEME_NAME_DUPLICATED("이미 존재하는 테마입니다."),
    THEME_NOT_EXIST("존재하지 않는 테마입니다."),
    RESERVED_THEME("해당 테마의 예약이 존재합니다."),

    // 예약
    RESERVATION_NOT_EXIST("존재하지 않는 예약입니다."),
    RESERVATION_ALREADY_EXIST("이미 예약된 시간입니다."),
    RESERVATION_PAST("과거로 예약할 수 없습니다."),
    MEMBER_ALREADY_RESERVED("이미 예약을 등록하였습니다."),
    WAITING_WITHOUT_RESERVATION("대기 예약은 기존 예약이 있을 때만 가능합니다."),
    DENY_NOT_PENDING("확정 예약은 거절할 수 없습니다."),

    // 예약 시간
    RESERVATION_TIME_NOT_EXIST("존재하지 않는 예약 시간입니다."),
    RESERVATION_TIME_ALREADY_EXIST("해당 예약 시간은 이미 존재합니다."),
    RESERVED_RESERVATION_TIME("해당 예약 시간의 예약이 존재합니다."),

    // 멤버
    MEMBER_NOT_EXIST("존재하지 않는 멤버입니다."),
    EMAIL_DUPLICATED("중복된 이메일입니다."),
    NAME_DUPLICATED("중복된 이름입니다."),

    // 결제
    PAYMENT_APPROVE_FAILED("결제 승인 중 문제가 발생하여 결제를 취소했습니다."),
    PAYMENT_CANCELED("결제 중 문제가 발생하여 결제를 취소했습니다."),
    PAYMENT_CANCEL_SCHEDULED("결제 중 문제가 발생했습니다. 시간이 지나도 결제가 취소되지 않는다면 관리자에게 문의하세요."),
    PAYMENT_FAILED_UNKNOWN("결제 중 예기치 못한 문제가 발생하였습니다. 관리자에게 문의하세요."),
    PAYMENT_NOT_EXIST("존재하지 않는 결제 정보입니다.");

    private final String message;

    BusinessErrorCode(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
