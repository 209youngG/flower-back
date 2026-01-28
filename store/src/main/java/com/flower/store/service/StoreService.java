package com.flower.store.service;

import com.flower.store.domain.Store;
import com.flower.store.domain.StoreStatus;
import com.flower.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    public Long registerStore(Long memberId, String name, String address, Double lat, Double lon) {
        if (memberId == null) {
            throw new IllegalArgumentException("Member ID cannot be null");
        }

        Store store = Store.builder()
                .ownerId(memberId)
                .name(name)
                .address(address)
                .lat(lat)
                .lon(lon)
                .status(StoreStatus.PENDING)
                .build();

        Store savedStore = storeRepository.save(store);
        return savedStore.getId();
    }
}
