package roomescape.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.controller.api.AdminTimeApi;
import roomescape.dto.request.ReservationTimeRequest;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.global.auth.LoginRequired;
import roomescape.global.auth.RoleRequired;
import roomescape.service.reservation.ReservationTimeService;

import java.util.List;

import static roomescape.domain.member.MemberRole.ADMIN;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminTimeController implements AdminTimeApi {

    private final ReservationTimeService reservationTimeService;

    @Override
    @LoginRequired
    @RoleRequired(ADMIN)
    @PostMapping("/times")
    public ResponseEntity<ReservationTimeResponse> save(@RequestBody @Valid ReservationTimeRequest request) {
        ReservationTimeResponse response = reservationTimeService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @LoginRequired
    @RoleRequired(ADMIN)
    @GetMapping("/times")
    public ResponseEntity<List<ReservationTimeResponse>> getAll() {
        List<ReservationTimeResponse> response = reservationTimeService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    @LoginRequired
    @RoleRequired(ADMIN)
    @DeleteMapping("/times/{timeId}")
    public ResponseEntity<Void> remove(@PathVariable long timeId) {
        reservationTimeService.remove(timeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
