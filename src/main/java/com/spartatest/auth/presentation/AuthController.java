package com.spartatest.auth.presentation;

import com.spartatest.auth.application.AccountService;
import com.spartatest.auth.application.ReissueService;
import com.spartatest.auth.domain.entity.User;
import com.spartatest.auth.dto.GrantAdminResponse;
import com.spartatest.auth.dto.LoginRequest;
import com.spartatest.auth.dto.SignupRequest;
import com.spartatest.auth.dto.SignupResponse;
import com.spartatest.common.annotation.CurrentUser;
import com.spartatest.common.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AUTH API", description = "회원가입, 회원탈퇴, 로그인, 로그아웃, 권한요청")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;
    private final ReissueService reissueService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "아이디, 비밀번호, 닉네임을 이용한 회원가입 요청합니다.")
    @ApiResponse(responseCode = "200", description = "회원 가입 성공")
    public ResponseEntity<ApiResult<SignupResponse>> signup(
            @RequestBody @Valid SignupRequest request
    ) {
        return ResponseEntity.ok(ApiResult.success(accountService.signup(request), "회원 가입이 완료되었습니다."));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "회원탈퇴", description = "계정을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공")
    public ResponseEntity<ApiResult<Void>> deleteAccount(
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        accountService.deleteAccount(user.getId());
        return ResponseEntity.ok(ApiResult.success("회원 탈퇴가 완료되었습니다."));
    }

    @PatchMapping("/admin/{targetUsername}/roles")
    @Operation(summary = "관리자 권한 부여", description = "특정 사용자에게 관리자 역할을 부여합니다.", security = @SecurityRequirement(name = "Authorization"))
    @ApiResponse(responseCode = "200", description = "권한 부여 성공")
    public ResponseEntity<ApiResult<GrantAdminResponse>> grantAdminRole(
            @Parameter(description = "권한을 부여할 유저의 아이디") @PathVariable String targetUsername,
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        return ResponseEntity.ok(ApiResult.success(accountService.grantAdminRole(targetUsername, user), "관리자 권한이 부여되었습니다."));
    }

    @PostMapping("/login")
    @Operation(
            summary = "로그인 (슈퍼어드민 -> username: admin, password: admin1234)",
            description = """
                admin 계정으로 로그인할 경우 username: admin, password: admin1234 를 입력하세요.
                """
    )
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    public void login(
            @RequestBody LoginRequest request
    ) {
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 로그인한 사용자의 로그아웃 처리합니다.")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    public void logout(
            @Parameter(hidden = true) @CurrentUser User user
    ) {
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "개발자 도구를 열어서 로그인을 실행한 뒤 set-cookie의 값을 넣어주세요.", parameters = {
        @Parameter(name = "Cookie", description = "refresh=your-refresh-token", in = ParameterIn.HEADER, required = true)
    })
    public ResponseEntity<ApiResult<Void>> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        reissueService.reissue(request, response);
        return ResponseEntity.ok(ApiResult.success("토큰이 재발급되었습니다."));
    }
}