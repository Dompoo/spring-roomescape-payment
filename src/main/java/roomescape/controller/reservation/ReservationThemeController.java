package roomescape.controller.reservation;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.request.ReservationThemeRequest;
import roomescape.dto.response.ReservationThemeResponse;
import roomescape.dto.response.ReservationTimeWithAvailabilityResponse;
import roomescape.service.reservation.ReservationThemeService;

@RequiredArgsConstructor
@RequestMapping("/themes")
@RestController
public class ReservationThemeController {

    private final ReservationThemeService reservationThemeService;

    @GetMapping()
    public ResponseEntity<List<ReservationThemeResponse>> reservationThemeList() {
        return ResponseEntity.status(HttpStatus.OK).body(reservationThemeService.findReservationThemes());
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<ReservationThemeResponse>> reservationThemeRankingList() {
        return ResponseEntity.status(HttpStatus.OK).body(reservationThemeService.findPopularThemes());
    }

    @PostMapping()
    public ResponseEntity<ReservationThemeResponse> reservationThemeAdd(
            @RequestBody ReservationThemeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationThemeService.addReservationTheme(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> reservationThemeRemove(@PathVariable(name = "id") long id) {
        reservationThemeService.removeReservationTheme(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/available-times")
    public ResponseEntity<List<ReservationTimeWithAvailabilityResponse>> reservationTimeOfTheme(@PathVariable Long id, @RequestParam LocalDate date) {
        return ResponseEntity.ok(reservationThemeService.findReservationTimeOfTheme(id, date));
    }
}
