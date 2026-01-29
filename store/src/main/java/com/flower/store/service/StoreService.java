package com.flower.store.service;

import com.flower.common.exception.BusinessException;
import com.flower.common.exception.EntityNotFoundException;
import com.flower.store.domain.Store;
import com.flower.store.domain.StoreStatus;
import com.flower.store.dto.RegisterStoreRequest;
import com.flower.store.dto.StoreDto;
import com.flower.store.dto.UpdateStoreRequest;
import com.flower.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private static final double EARTH_RADIUS_KM = 6371.0;

    @Transactional
    public Long registerStore(Long memberId, RegisterStoreRequest request) {
        if (memberId == null) {
            throw new IllegalArgumentException("Member ID cannot be null");
        }

        Store store = Store.builder()
                .ownerId(memberId)
                .name(request.name())
                .address(request.address())
                .lat(request.lat())
                .lon(request.lon())
                .phone(request.phone())
                .description(request.description())
                .openTime(request.openTime())
                .closeTime(request.closeTime())
                .closedDays(request.closedDays())
                .status(StoreStatus.PENDING)
                .build();

        Store savedStore = storeRepository.save(store);
        return savedStore.getId();
    }

    @Transactional(readOnly = true)
    public StoreDto getStoreById(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("매장을 찾을 수 없습니다. ID: " + storeId));
        return StoreDto.from(store);
    }

    @Transactional(readOnly = true)
    public StoreDto getMyStore(Long memberId) {
        Store store = storeRepository.findByOwnerId(memberId)
                .orElseThrow(() -> new EntityNotFoundException("등록된 매장이 없습니다."));
        return StoreDto.from(store);
    }

    @Transactional(readOnly = true)
    public List<StoreDto> getNearbyStores(Double lat, Double lon, Double radiusKm) {
        List<Store> allStores = storeRepository.findByStatus(StoreStatus.APPROVED);
        
        return allStores.stream()
                .filter(store -> calculateDistance(lat, lon, store.getLat(), store.getLon()) <= radiusKm)
                .map(StoreDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("매장을 찾을 수 없습니다. ID: " + storeId));
        store.approve();
    }

    @Transactional
    public void rejectStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("매장을 찾을 수 없습니다. ID: " + storeId));
        store.reject();
    }

    @Transactional
    public void updateStore(Long memberId, UpdateStoreRequest request) {
        Store store = storeRepository.findByOwnerId(memberId)
                .orElseThrow(() -> new EntityNotFoundException("등록된 매장이 없습니다."));
        
        store.updateInfo(
                request.name(),
                request.address(),
                request.lat(),
                request.lon(),
                request.phone(),
                request.description(),
                request.openTime(),
                request.closeTime(),
                request.closedDays()
        );
    }

    @Transactional(readOnly = true)
    public List<StoreDto> getPendingStores() {
        return storeRepository.findByStatus(StoreStatus.PENDING).stream()
                .map(StoreDto::from)
                .collect(Collectors.toList());
    }

    /**
     * Haversine 공식을 이용한 두 좌표 간 거리 계산 (단위: km)
     */
    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
