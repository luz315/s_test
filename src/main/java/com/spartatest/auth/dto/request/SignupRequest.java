package com.spartatest.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "아이디는 필수 입력값입니다.")
        @Size(min = 4, max = 10, message = "아이디는 4자 이상 10자 이하로 입력해주세요.")
        @Schema(description = "사용자 아이디 (4~10자)")
        String username,

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Size(min = 4, max = 10, message = "비밀번호는 4자 이상 10자 이하로 입력해주세요.")
        @Schema(description = "비밀번호 (4~10자)")
        String password,

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
        @Schema(description = "닉네임 (2~10자)")
        String nickname
) {}
