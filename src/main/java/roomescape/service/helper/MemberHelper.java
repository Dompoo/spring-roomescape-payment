package roomescape.service.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.global.exception.business.impl.NotFoundException;

import static roomescape.global.exception.business.BusinessErrorCode.MEMBER_NOT_EXIST;

@RequiredArgsConstructor
@Service
public class MemberHelper {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Member getById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(MEMBER_NOT_EXIST));
    }

    @Transactional(readOnly = true)
    public Member getMemberByEmail(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MEMBER_NOT_EXIST));
    }

    @Transactional(readOnly = true)
    public boolean isExist(long memberId) {
        return memberRepository.existById(memberId);
    }
}
