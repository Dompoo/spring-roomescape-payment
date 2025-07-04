package roomescape.domain.member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member save(final Member member);

    List<Member> findAll();

    Optional<Member> findByEmail(final String email);

    Optional<Member> findById(final long id);

    boolean existById(long memberId);

    boolean existByEmail(final String email);

    boolean existByName(final String name);
}
