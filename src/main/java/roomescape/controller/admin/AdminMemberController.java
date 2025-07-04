package roomescape.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.api.AdminMemberApi;
import roomescape.dto.response.MemberResponse;
import roomescape.global.auth.LoginRequired;
import roomescape.global.auth.RoleRequired;
import roomescape.service.member.MemberService;

import java.util.List;

import static roomescape.domain.member.MemberRole.ADMIN;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminMemberController implements AdminMemberApi {

    private final MemberService memberService;

    @Override
    @LoginRequired
    @RoleRequired(ADMIN)
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> getAll() {
        List<MemberResponse> response = memberService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
