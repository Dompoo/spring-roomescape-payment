package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.request.MemberRegisterRequest;
import roomescape.dto.response.LoginResponse;
import roomescape.dto.response.MemberRegisterResponse;
import roomescape.global.auth.LoginInfo;

import javax.naming.AuthenticationException;

@Tag(name = "1. 보안")
public interface MemberAuthApi {

    @Operation(summary = "회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(examples = {
                    @ExampleObject(
                            name = "이메일이 중복된 경우",
                            ref = "#/components/examples/EMAIL_DUPLICATED"
                    ),
                    @ExampleObject(
                            name = "이름이 중복된 경우",
                            ref = "#/components/examples/NAME_DUPLICATED"
                    ),
            })),
    })
    ResponseEntity<MemberRegisterResponse> register(
            @RequestBody(required = true) MemberRegisterRequest request
    );

    @Operation(summary = "로그인")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(examples = {
                    @ExampleObject(
                            name = "비밀번호가 일치하지 않는 경우",
                            ref = "#/components/examples/INVALID_PASSWORD"
                    )
            })),
            @ApiResponse(responseCode = "404", content = @Content(examples = {
                    @ExampleObject(
                            name = "존재하지 않는 이메일인 경우",
                            ref = "#/components/examples/MEMBER_NOT_EXIST"
                    )
            })),
    })
    ResponseEntity<Void> login(
            @RequestBody(required = true) LoginRequest loginRequest,
            @Parameter(hidden = true) HttpSession session
    ) throws AuthenticationException;

    @Operation(summary = "로그인 정보 확인")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
    })
    ResponseEntity<LoginResponse> loginCheck(
            @Parameter(hidden = true) LoginInfo loginInfo
    );

    @Operation(summary = "로그아웃")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
    })
    ResponseEntity<Void> logout(
            @Parameter(hidden = true) HttpSession session
    );
}
