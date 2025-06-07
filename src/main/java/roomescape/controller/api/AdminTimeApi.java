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
import roomescape.dto.request.ReservationTimeRequest;
import roomescape.dto.response.ReservationTimeResponse;

import java.util.List;

@Tag(name = "4. 예약 시간")
public interface AdminTimeApi {

    @Operation(summary = "예약 시간 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(examples = {
                    @ExampleObject(
                            name = "중복된 시간인 경우",
                            ref = "#/components/examples/RESERVATION_TIME_ALREADY_EXIST"
                    ),
            })),
    })
    ResponseEntity<ReservationTimeResponse> save(
            @RequestBody(required = true) ReservationTimeRequest request
    );

    @Operation(summary = "모든 예약 시간 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
    })
    ResponseEntity<List<ReservationTimeResponse>> getAll();

    @Operation(summary = "예약 시간 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(examples = {
                    @ExampleObject(
                            name = "예약이 존재하는 예약 시간인 경우",
                            ref = "#/components/examples/RESERVED_RESERVATION_TIME"
                    ),
            })),
            @ApiResponse(responseCode = "404", content = @Content(examples = {
                    @ExampleObject(
                            name = "id에 해당하는 예약 시간이 없는 경우",
                            ref = "#/components/examples/RESERVATION_TIME_NOT_EXIST"
                    )
            })),
    })
    ResponseEntity<Void> remove(
            @Parameter(example = "1", required = true) long timeId
    );
}
