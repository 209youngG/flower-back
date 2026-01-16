package com.flower.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주소 추가 요청")
public record AddAddressRequest(
    @Schema(description = "수령인 이름", example = "홍길동")
    String recipientName,

    @Schema(description = "수령인 전화번호", example = "010-1234-5678")
    String recipientPhone,

    @Schema(description = "우편번호", example = "12345")
    String zipCode,

    @Schema(description = "기본 주소", example = "서울시 강남구 테헤란로 123")
    String street,

    @Schema(description = "상세 주소", example = "101호")
    String city,
    
    @Schema(description = "기본 배송지 여부")
    boolean isDefault
) {}
