package roomescape.global.auth;

public record LoginInfo(
        long memberId
) {
    public static LoginInfo fromObject(Object memberId) {
        return new LoginInfo((long) memberId);
    }
}
