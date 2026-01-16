package com.flower.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 가입 요청")
public record RegisterMemberRequest(
    @Schema(description = "로그인 ID", example = "user123")
    String loginId,

    @Schema(description = "이메일", example = "user@example.com")
    String email,

    @Schema(description = "비밀번호", example = "password123")
    String password,

    @Schema(description = "이름", example = "홍길동")
    String name,

    @Schema(description = "전화번호", example = "010-1234-5678")
    String phoneNumber,

    @Schema(description = "우편번호", example = "12345")
    String zipCode,

    @Schema(description = "기본 주소 (시/구/동)", example = "서울시 강남구 테헤란로 123")
    String address,

    @Schema(description = "상세 주소", example = "101호")
    String detailAddress
) {}
