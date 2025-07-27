package com.spartatest.auth.presentation;

import com.spartatest.auth.application.AccountService;
import com.spartatest.auth.domain.entity.User;
import com.spartatest.auth.dto.request.SignupRequest;
import com.spartatest.auth.dto.response.SignupResponse;
import com.spartatest.common.annotation.CurrentUser;
import com.spartatest.common.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ACCOUNT API", description = "회원가입, 회원탈퇴")
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

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
}