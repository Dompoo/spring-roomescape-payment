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
import roomescape.dto.request.ReservationPendingRequest;
import roomescape.dto.request.ReservationRequest;
import roomescape.dto.response.MyPageReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.global.auth.LoginInfo;

import javax.naming.AuthenticationException;
import java.util.List;

@Tag(name = "2. 예약")
public interface MemberReservationApi {

    @Operation(summary = "결제 승인 및 예약")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(examples = {
                    @ExampleObject(
                            name = "이미 예약을 등록한 경우",
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
            @ApiResponse(responseCode = "500", content = @Content(examples = {
                    @ExampleObject(
                            name = "토스 결제 승인 중 승인에 문제가 발생하여 승인에 실패한 경우",
                            ref = "#/components/examples/PAYMENT_APPROVE_FAILED"
                    ),
                    @ExampleObject(
                            name = "토스 결제 승인 중 문제가 발생하여 결제 취소된 경우",
                            ref = "#/components/examples/PAYMENT_CANCELED"
                    ),
                    @ExampleObject(
                            name = "토스 결제 승인 중 문제가 발생하여 결제 취소가 스케쥴링된 경우",
                            ref = "#/components/examples/PAYMENT_CANCEL_SCHEDULED"
                    ),
                    @ExampleObject(
                            name = "토스 결제 승인 중 문제가 발생하였지만 복구하지 못한 경우",
                            ref = "#/components/examples/PAYMENT_FAILED_UNKNOWN"
                    ),
            })),
    })
    ResponseEntity<ReservationResponse> reserve(
            @RequestBody(required = true) ReservationRequest request,
            @Parameter(hidden = true) LoginInfo loginInfo
    );

    @Operation(summary = "예약 대기")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(examples = {
                    @ExampleObject(
                            name = "이미 예약을 등록한 경우",
                            ref = "#/components/examples/MEMBER_ALREADY_RESERVED"
                    ),
                    @ExampleObject(
                            name = "예약이 존재하지 않는데, 대기만 등록하려고 하는 경우",
                            ref = "#/components/examples/WAITING_WITHOUT_RESERVATION"
                    ),
            })),
    })
    ResponseEntity<ReservationResponse> addPending(
            @RequestBody(required = true) ReservationPendingRequest request,
            @Parameter(hidden = true) LoginInfo loginInfo
    );

    @Operation(summary = "내 예약 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
    })
    ResponseEntity<List<MyPageReservationResponse>> getMines(
            @Parameter(hidden = true) LoginInfo loginInfo
    );

    @Operation(summary = "내 예약 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "401", content = @Content(examples = {
                    @ExampleObject(
                            name = "내 예약이 아닌 경우",
                            ref = "#/components/examples/AUTHORITY_LACK"
                    )
            })),
            @ApiResponse(responseCode = "404", content = @Content(examples = {
                    @ExampleObject(
                            name = "id에 해당하는 예약이 없는 경우",
                            ref = "#/components/examples/RESERVATION_NOT_EXIST"
                    )
            })),
    })
    ResponseEntity<Void> remove(
            @Parameter(example = "1", required = true) long reservationId,
            @Parameter(hidden = true) LoginInfo loginInfo
    ) throws AuthenticationException;
}
