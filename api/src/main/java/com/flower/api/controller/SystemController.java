package com.flower.api.controller;

import com.flower.product.domain.Product.DeliveryType;
import com.flower.product.domain.ProductCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/system")
@Tag(name = "System", description = "시스템 공통 코드 API")
public class SystemController {

    @Operation(summary = "공통 코드 조회", description = "카테고리, 배송 타입 등 시스템 공통 코드를 조회합니다.")
    @GetMapping("/codes")
    public ResponseEntity<SystemCodesDto> getSystemCodes() {
        List<CodeItemDto> categories = Arrays.stream(ProductCategory.values())
                .map(c -> new CodeItemDto(c.getDisplayName(), c.name(), 0))
                .collect(Collectors.toList());

        List<CodeItemDto> deliveryTypes = Arrays.stream(DeliveryType.values())
                .map(d -> new CodeItemDto(d.getDescription(), d.name(), 0))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new SystemCodesDto(categories, deliveryTypes));
    }

    @Getter
    @Builder
    public static class SystemCodesDto {
        private List<CodeItemDto> category;
        private List<CodeItemDto> deliveryType;

        public SystemCodesDto(List<CodeItemDto> category, List<CodeItemDto> deliveryType) {
            this.category = category;
            this.deliveryType = deliveryType;
        }
    }

    @Getter
    @Builder
    public static class CodeItemDto {
        private String label;
        private String value;
        private Integer order;

        public CodeItemDto(String label, String value, Integer order) {
            this.label = label;
            this.value = value;
            this.order = order;
        }
    }
}
