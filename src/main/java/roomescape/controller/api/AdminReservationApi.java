package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.response.PendingReservationResponse;
import roomescape.dto.response.ReservationResponse;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "2. 예약")
public interface AdminReservationApi {

    @Operation(summary = "예약 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(examples = {
                    @ExampleObject(
                            name = "해당 멤버가 이미 예약을 등록한 경우",
                            ref = "#/components/examples/MEMBER_ALREADY_RESERVED"
                    ),
                    @ExampleObject(
                            name = "이미 예약된 시간인 경우",
                            ref = "#/components/examples/RESERVATION_ALREADY_EXIST"
                    ),
                    @ExampleObject(
                            name = "과거 시간으로 예약하는 경우",
                            ref = "#/components/examples/RESERVATION_PAST"
                    ),
            })),
    })
    ResponseEntity<ReservationResponse> save(
            @RequestBody(required = true) CreateReservationRequest request
    );

    @Operation(summary = "예약 필터링 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
    })
    ResponseEntity<List<ReservationResponse>> getAllByFilter(
            @Parameter(example = "1") Long memberId,
            @Parameter(example = "1") Long themeId,
            @Parameter(example = "2025-06-03") LocalDate dateFrom,
            @Parameter(example = "2025-06-07") LocalDate dateTo
    );

    @Operation(summary = "대기 예약 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
    })
    ResponseEntity<List<PendingReservationResponse>> getAllPendings();

    @Operation(summary = "예약 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", content = @Content(examples = {
                    @ExampleObject(
                            name = "id에 해당하는 예약이 없는 경우",
                            ref = "#/components/examples/RESERVATION_NOT_EXIST"
                    ),
            })),
    })
    ResponseEntity<Void> remove(
            @Parameter(example = "1", required = true) long reservationId
    );

    @Operation(summary = "대기 예약 거절")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(examples = {
                    @ExampleObject(
                            name = "거절하려는 예약이 대기 예약이 아닌 경우",
                            ref = "#/components/examples/DENY_NOT_PENDING"
                    ),
            })),
            @ApiResponse(responseCode = "404", content = @Content(examples = {
                    @ExampleObject(
                            name = "id에 해당하는 예약이 없는 경우",
                            ref = "#/components/examples/RESERVATION_NOT_EXIST"
                    )
            })),
    })
    ResponseEntity<Void> denyPending(
            @Parameter(example = "1", required = true) long reservationId
    );
}
