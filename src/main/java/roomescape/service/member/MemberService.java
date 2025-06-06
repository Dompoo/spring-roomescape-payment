package roomescape.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.member.MemberRole;
import roomescape.dto.request.MemberRegisterRequest;
import roomescape.dto.response.MemberRegisterResponse;
import roomescape.dto.response.MemberResponse;
import roomescape.global.exception.business.impl.InvalidCreateArgumentException;

import java.util.List;

import static roomescape.global.exception.business.BusinessErrorCode.EMAIL_DUPLICATED;
import static roomescape.global.exception.business.BusinessErrorCode.NAME_DUPLICATED;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberRegisterResponse register(final MemberRegisterRequest request) {
        validateDuplicateEmail(request.email());
        validateDuplicateName(request.name());
        final Member newMember = Member.builder()
                .email(request.email())
                .name(request.name())
                .password(request.password())
                .role(MemberRole.USER)
                .build();
        return MemberRegisterResponse.from(memberRepository.save(newMember));
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getAll() {
        return memberRepository.findAll().stream()
                .map(MemberResponse::from)
                .toList();
    }

    private void validateDuplicateEmail(final String email) {
        if (memberRepository.existByEmail(email)) {
            throw new InvalidCreateArgumentException(EMAIL_DUPLICATED);
        }
    }

    private void validateDuplicateName(final String name) {
        if (memberRepository.existByName(name)) {
            throw new InvalidCreateArgumentException(NAME_DUPLICATED);
        }
    }
}
