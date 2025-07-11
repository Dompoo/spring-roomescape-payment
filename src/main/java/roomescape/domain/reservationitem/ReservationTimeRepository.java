package roomescape.domain.reservationitem;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationTimeRepository {

    Optional<ReservationTime> findById(Long timeId);

    List<ReservationTime> findAll();

    ReservationTime save(final ReservationTime reservationTime);

    boolean existsByStartAt(final LocalTime startAt);

    void deleteById(final long id);

    boolean isAvailableToRemove(long id);

    boolean existsById(long id);
}
