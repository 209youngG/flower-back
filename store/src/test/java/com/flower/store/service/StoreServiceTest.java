package com.flower.store.service;

import com.flower.store.domain.Store;
import com.flower.store.domain.StoreStatus;
import com.flower.store.dto.RegisterStoreRequest;
import com.flower.store.repository.StoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @InjectMocks
    private StoreService storeService;

    @Mock
    private StoreRepository storeRepository;

    @Test
    @DisplayName("상점 등록 성공")
    void should_registerStore_when_validRequest() {
        // given
        Long memberId = 1L;
        RegisterStoreRequest request = new RegisterStoreRequest(
            "My Flower Shop",
            "Seoul, Gangnam",
            37.5,
            127.0,
            "02-1234-5678",
            "Best flower shop",
            java.time.LocalTime.of(9, 0),
            java.time.LocalTime.of(21, 0),
            java.util.List.of("Sunday")
        );

        Store savedStore = Store.builder()
                .ownerId(memberId)
                .name(request.name())
                .address(request.address())
                .lat(request.lat())
                .lon(request.lon())
                .status(StoreStatus.PENDING)
                .build();
        
        given(storeRepository.save(any(Store.class))).willReturn(savedStore);

        // when
        storeService.registerStore(memberId, request);

        // then
        verify(storeRepository).save(any(Store.class));
    }
}
