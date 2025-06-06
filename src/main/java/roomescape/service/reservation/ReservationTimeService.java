package roomescape.service.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.domain.reservationitem.ReservationTimeRepository;
import roomescape.dto.request.ReservationTimeRequest;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.dto.response.ReservationTimeWithAvailabilityResponse;
import roomescape.global.exception.business.impl.InvalidCreateArgumentException;
import roomescape.global.exception.business.impl.NotFoundException;
import roomescape.global.exception.business.impl.RelatedEntityExistException;
import roomescape.service.helper.ReservationItemHelper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static roomescape.global.exception.business.BusinessErrorCode.*;

@RequiredArgsConstructor
@Service
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationItemHelper itemHelper;
    private final ReservationItemHelper reservationItemHelper;

    @Transactional
    public ReservationTimeResponse save(final ReservationTimeRequest request) {
        ReservationTime reservationTime = new ReservationTime(request.startAt());
        validateUniqueReservationTime(reservationTime);
        ReservationTime saved = reservationTimeRepository.save(reservationTime);
        return ReservationTimeResponse.from(saved);
    }

    private void validateUniqueReservationTime(final ReservationTime reservationTime) {
        final LocalTime startAt = reservationTime.getStartAt();
        if (reservationTimeRepository.existsByStartAt(startAt)) {
            throw new InvalidCreateArgumentException(RESERVATION_TIME_ALREADY_EXIST);
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationTimeResponse> getAll() {
        return reservationTimeRepository.findAll().stream()
                .map(ReservationTimeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationTimeWithAvailabilityResponse> getAllWithAvailabilityBy(long themeId, LocalDate date) {
        List<ReservationTime> availableReservationTime = reservationTimeRepository.findAll();
        return availableReservationTime.stream()
                .map(reservationTime -> {
                            boolean isBooked = itemHelper.isExistReservationItem(date, reservationTime.getId(), themeId);
                            return ReservationTimeWithAvailabilityResponse.from(reservationTime, isBooked);
                        }
                ).toList();
    }

    @Transactional
    public void remove(final long id) {
        if (!reservationTimeRepository.existsById(id)) {
            throw new NotFoundException(RESERVATION_TIME_NOT_EXIST);
        }
        if (!reservationTimeRepository.isAvailableToRemove(id)) {
            throw new RelatedEntityExistException(RESERVED_RESERVATION_TIME);
        }
        reservationItemHelper.deleteAllReservationItemByTimeId(id);
        reservationTimeRepository.deleteById(id);
    }
}
