package com.flower.store.repository;

import com.flower.store.domain.Store;
import com.flower.store.domain.StoreStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByOwnerId(Long ownerId);
    List<Store> findByStatus(StoreStatus status);
}
