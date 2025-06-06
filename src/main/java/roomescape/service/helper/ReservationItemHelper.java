package roomescape.service.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservationitem.*;
import roomescape.global.exception.business.impl.NotFoundException;

import java.time.LocalDate;

import static roomescape.global.exception.business.BusinessErrorCode.RESERVATION_TIME_NOT_EXIST;
import static roomescape.global.exception.business.BusinessErrorCode.THEME_NOT_EXIST;

@RequiredArgsConstructor
@Service
public class ReservationItemHelper {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationThemeRepository reservationThemeRepository;
    private final ReservationItemRepository reservationItemRepository;

    @Transactional
    public ReservationItem getOrCreate(LocalDate date, long timeId, long themeId) {
        return reservationItemRepository.findReservationItemByDateAndTimeIdAndThemeId(date, timeId, themeId)
                .orElseGet(() -> create(date, timeId, themeId));
    }

    private ReservationItem create(LocalDate date, long timeId, long themeId) {
        final ReservationTime time = reservationTimeRepository.findById(timeId)
                .orElseThrow(() -> new NotFoundException(RESERVATION_TIME_NOT_EXIST));
        final ReservationTheme theme = reservationThemeRepository.findById(themeId)
                .orElseThrow(() -> new NotFoundException(THEME_NOT_EXIST));

        final ReservationItem item = new ReservationItem(date, time, theme);
        return reservationItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public boolean isExistReservationItem(final LocalDate date, final Long timeId, final Long themeId) {
        return reservationItemRepository.existsByDateAndTimeAndTheme(date, timeId, themeId);
    }

    @Transactional
    public void deleteAllReservationItemByThemeId(long themeId) {
        reservationItemRepository.deleteAllReservationItemByThemeId(themeId);
    }

    @Transactional
    public void deleteAllReservationItemByTimeId(long timeId) {
        reservationItemRepository.deleteAllReservationItemByTimeId(timeId);
    }
}
