package com.spartatest.auth.presentation;

import com.spartatest.auth.application.AccountService;
import com.spartatest.auth.application.ReissueService;
import com.spartatest.auth.domain.entity.User;
import com.spartatest.auth.dto.request.LoginRequest;
import com.spartatest.common.annotation.CurrentUser;
import com.spartatest.common.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AUTH API", description = "로그인, 로그아웃, 토큰 재발급")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AccountService accountService;
    private final ReissueService reissueService;

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