package com.flower.member.dto;

import com.flower.member.domain.MemberGrade;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "회원 정보 응답")
public record MemberDto(
    @Schema(description = "회원 ID", example = "1")
    Long id,

    @Schema(description = "이메일", example = "user@example.com")
    String email,

    @Schema(description = "이름", example = "홍길동")
    String name,

    @Schema(description = "전화번호", example = "010-1234-5678")
    String phoneNumber,

    @Schema(description = "포인트 잔액", example = "1000")
    BigDecimal pointBalance,

    @Schema(description = "회원 등급", example = "BRONZE")
    MemberGrade grade
) {}
