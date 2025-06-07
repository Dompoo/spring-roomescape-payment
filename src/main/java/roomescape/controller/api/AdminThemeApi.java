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
import roomescape.dto.request.ReservationThemeRequest;
import roomescape.dto.response.ReservationThemeResponse;

@Tag(name = "3. 테마")
public interface AdminThemeApi {

    @Operation(summary = "테마 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(examples = {
                    @ExampleObject(
                            name = "중복된 테마 이름인 경우",
                            ref = "#/components/examples/THEME_NAME_DUPLICATED"
                    )
            })),
    })
    ResponseEntity<ReservationThemeResponse> save(
            @RequestBody(required = true) ReservationThemeRequest request
    );

    @Operation(summary = "테마 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(examples = {
                    @ExampleObject(
                            name = "예약이 존재하는 테마인 경우",
                            ref = "#/components/examples/RESERVED_THEME"
                    )
            })),
            @ApiResponse(responseCode = "404", content = @Content(examples = {
                    @ExampleObject(
                            name = "id에 해당하는 테마가 없는 경우",
                            ref = "#/components/examples/THEME_NOT_EXIST"
                    )
            })),
    })
    ResponseEntity<Void> remove(
            @Parameter(example = "1", required = true) long themeId
    );
}
