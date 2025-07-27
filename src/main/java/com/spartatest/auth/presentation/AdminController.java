package com.spartatest.auth.presentation;

import com.spartatest.auth.application.AdminService;
import com.spartatest.auth.domain.entity.User;
import com.spartatest.auth.dto.response.GrantAdminResponse;
import com.spartatest.common.annotation.CurrentUser;
import com.spartatest.common.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ADMIN API", description = "관리자 권한 부여")
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/admin/{targetUsername}/roles")
    @Operation(summary = "관리자 권한 부여", description = "특정 사용자에게 관리자 역할을 부여합니다.", security = @SecurityRequirement(name = "Authorization"))
    @ApiResponse(responseCode = "200", description = "권한 부여 성공")
    public ResponseEntity<ApiResult<GrantAdminResponse>> grantAdminRole(
            @Parameter(description = "권한을 부여할 유저의 아이디") @PathVariable String targetUsername,
            @Parameter(hidden = true) @CurrentUser User user
    ) {
        return ResponseEntity.ok(ApiResult.success(adminService.grantAdminRole(targetUsername, user), "관리자 권한이 부여되었습니다."));
    }
}
