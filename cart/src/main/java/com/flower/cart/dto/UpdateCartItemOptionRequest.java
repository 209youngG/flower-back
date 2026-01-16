package com.flower.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "장바구니 아이템 옵션 수정 요청")
public record UpdateCartItemOptionRequest(
    @Schema(description = "변경할 옵션 ID 목록")
    List<Long> optionIds
) {}
