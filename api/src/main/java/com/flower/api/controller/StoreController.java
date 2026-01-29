package com.flower.api.controller;

import com.flower.api.security.UserPrincipal;
import com.flower.store.dto.RegisterStoreRequest;
import com.flower.store.dto.StoreDto;
import com.flower.store.dto.UpdateStoreRequest;
import com.flower.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Store", description = "매장 관리 API")
@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @Operation(summary = "매장 등록", description = "사장님이 새 매장을 등록합니다.")
    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ROOT_ADMIN')")
    public ResponseEntity<Map<String, Long>> registerStore(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody RegisterStoreRequest request) {
        Long storeId = storeService.registerStore(principal.getId(), request);
        return ResponseEntity.ok(Map.of("storeId", storeId));
    }

    @Operation(summary = "내 매장 조회", description = "로그인한 사장님의 매장 정보를 조회합니다.")
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('SELLER', 'STORE_ADMIN', 'ROOT_ADMIN')")
    public ResponseEntity<StoreDto> getMyStore(
            @AuthenticationPrincipal UserPrincipal principal) {
        StoreDto store = storeService.getMyStore(principal.getId());
        return ResponseEntity.ok(store);
    }

    @Operation(summary = "매장 상세 조회", description = "특정 매장의 상세 정보를 조회합니다.")
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreDto> getStoreById(@PathVariable Long storeId) {
        StoreDto store = storeService.getStoreById(storeId);
        return ResponseEntity.ok(store);
    }

    @Operation(summary = "내 주변 매장 검색", description = "현재 위치 기반으로 반경 내 매장을 검색합니다.")
    @GetMapping("/nearby")
    public ResponseEntity<List<StoreDto>> getNearbyStores(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(defaultValue = "5.0") Double radiusKm) {
        List<StoreDto> stores = storeService.getNearbyStores(lat, lon, radiusKm);
        return ResponseEntity.ok(stores);
    }

    @Operation(summary = "매장 정보 수정", description = "내 매장 정보를 수정합니다.")
    @PutMapping("/my")
    @PreAuthorize("hasAnyRole('SELLER', 'STORE_ADMIN', 'ROOT_ADMIN')")
    public ResponseEntity<Void> updateMyStore(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody UpdateStoreRequest request) {
        storeService.updateStore(principal.getId(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "승인 대기 매장 목록 조회", description = "관리자가 승인 대기 중인 매장 목록을 조회합니다.")
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ROOT_ADMIN')")
    public ResponseEntity<List<StoreDto>> getPendingStores() {
        List<StoreDto> stores = storeService.getPendingStores();
        return ResponseEntity.ok(stores);
    }

    @Operation(summary = "매장 승인", description = "관리자가 매장을 승인합니다.")
    @PostMapping("/{storeId}/approve")
    @PreAuthorize("hasRole('ROOT_ADMIN')")
    public ResponseEntity<Void> approveStore(@PathVariable Long storeId) {
        storeService.approveStore(storeId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "매장 거절", description = "관리자가 매장을 거절합니다.")
    @PostMapping("/{storeId}/reject")
    @PreAuthorize("hasRole('ROOT_ADMIN')")
    public ResponseEntity<Void> rejectStore(@PathVariable Long storeId) {
        storeService.rejectStore(storeId);
        return ResponseEntity.ok().build();
    }
}
