package roomescape.service.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservationitem.ReservationItem;
import roomescape.domain.reservationitem.ReservationItemRepository;
import roomescape.domain.reservationitem.ReservationTheme;
import roomescape.domain.reservationitem.ReservationTime;

import java.time.LocalDate;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class ReservationItemService {

    private final ReservationItemRepository reservationItemRepository;

    @Transactional
    public ReservationItem createReservationItemIfNotExist(LocalDate date,
                                                           ReservationTime reservationTime,
                                                           ReservationTheme theme) {
        if (isExistReservationItem(date, reservationTime, theme)) {
            return getReservationItemByDateAndTimeAndTheme(date, reservationTime, theme);
        }
        return addReservationItem(date, reservationTime, theme);
    }

    private ReservationItem addReservationItem(LocalDate date, ReservationTime reservationTime, ReservationTheme theme) {
        return reservationItemRepository.save(
                ReservationItem.builder()
                        .date(date)
                        .time(reservationTime)
                        .theme(theme)
                        .build()
        );
    }

    private ReservationItem getReservationItemByDateAndTimeAndTheme(LocalDate date,
                                                                    ReservationTime reservationTime,
                                                                    ReservationTheme theme) {
        return reservationItemRepository.findReservationItemByDateAndTimeAndTheme(date, reservationTime, theme)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 예약 항목을 찾지 못하였습니다."));
    }

    @Transactional
    public void deleteReservationItem(ReservationItem reservationItem) {
        reservationItemRepository.delete(reservationItem);
    }

    @Transactional(readOnly = true)
    public boolean isExistReservationItem(LocalDate date, ReservationTime reservationTime, ReservationTheme theme) {
        return reservationItemRepository.existsByDateAndTimeAndTheme(date, reservationTime, theme);
    }
}
