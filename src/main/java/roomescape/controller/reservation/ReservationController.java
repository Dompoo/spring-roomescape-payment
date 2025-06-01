package roomescape.controller.reservation;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.request.ReservationRequest;
import roomescape.dto.response.ReservationResponse;
import roomescape.service.reservation.ReservationService;
import roomescape.service.reservation.ReservingService;

@RequiredArgsConstructor
@RequestMapping("/reservations")
@RestController
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservingService reservingService;


    @GetMapping()
    public ResponseEntity<List<ReservationResponse>> reservationList() {
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.getAllReservations());
    }

    @PostMapping()
    public ResponseEntity<ReservationResponse> addReservation(@RequestBody @Valid final ReservationRequest request,
                                                              final Long memberId) {
        final ReservationResponse response = reservingService.reserve(
                request.date(),
                request.themeId(),
                request.timeId(),
                request.paymentKey(),
                request.orderId(),
                request.amount(),
                memberId
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/pending")
    public ResponseEntity<ReservationResponse> addPendingReservation(
            @RequestBody @Valid final ReservationRequest request, final Long memberId) {
        final CreateReservationRequest createReservationRequest = new CreateReservationRequest(
                memberId,
                request.date(),
                request.themeId(),
                request.timeId()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.addPendingReservation(createReservationRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeReservation(@PathVariable(name = "id") long id) {
        reservationService.removeReservation(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
