package roomescape.service.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservationitem.ReservationTheme;
import roomescape.domain.reservationitem.ReservationThemeRepository;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.dto.request.ReservationThemeRequest;
import roomescape.dto.response.ReservationThemeResponse;
import roomescape.dto.response.ReservationTimeWithAvailabilityResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class ReservationThemeService {

    public static final int POPULAR_THEME_AMOUNT = 10;
    public static final int POPULAR_THEME_DATE_FROM = 7;
    public static final int POPULAR_THEME_DATE_TO = 1;

    private final ReservationThemeRepository reservationThemeRepository;
    private final ReservationTimeService reservationTimeService;
    private final ReservationItemService reservationItemService;

    @Transactional(readOnly = true)
    public List<ReservationThemeResponse> findReservationThemes() {
        List<ReservationTheme> reservationThemes = reservationThemeRepository.findAll();
        return reservationThemes.stream().map(ReservationThemeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationThemeResponse> findPopularThemes() {
        List<ReservationTheme> popularReservationThemes = reservationThemeRepository.findWeeklyThemeOrderByCountDesc(
                POPULAR_THEME_AMOUNT,
                LocalDate.now().minusDays(POPULAR_THEME_DATE_FROM),
                LocalDate.now().minusDays(POPULAR_THEME_DATE_TO)
        );
        return popularReservationThemes.stream().map(ReservationThemeResponse::from).toList();
    }

    @Transactional
    public ReservationThemeResponse addReservationTheme(final ReservationThemeRequest request) {
        final ReservationTheme reservationTheme = ReservationTheme.builder()
                .name(request.name())
                .description(request.description())
                .thumbnail(request.thumbnail())
                .build();
        validateUniqueThemes(reservationTheme);
        ReservationTheme saved = reservationThemeRepository.save(reservationTheme);
        return ReservationThemeResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<ReservationTimeWithAvailabilityResponse> findReservationTimeOfTheme(long themeId, LocalDate date) {
        final ReservationTheme theme = getThemeById(themeId);

        List<ReservationTime> availableReservationTime = reservationTimeService.findReservationTimes();
        return availableReservationTime.stream()
                .map(reservationTime -> {
                            boolean isBooked = reservationItemService.isExistReservationItem(date, reservationTime, theme);
                            return ReservationTimeWithAvailabilityResponse.from(reservationTime, isBooked);
                        }
                ).toList();
    }

    @Transactional
    public void removeReservationTheme(final long id) {
        final ReservationTheme theme = getThemeById(id);
        try {
            reservationThemeRepository.deleteById(theme.getId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("[ERROR] 예약이 존재해 테마를 삭제할 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public ReservationTheme getThemeById(long id) {
        return reservationThemeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 테마입니다."));
    }

    private void validateUniqueThemes(final ReservationTheme reservationTheme) {
        if (reservationThemeRepository.existsByName(reservationTheme.getName())) {
            throw new IllegalArgumentException("[ERROR] 이미 존재하는 테마 입니다.");
        }
    }
}
