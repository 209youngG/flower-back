package com.flower.store.service;

import com.flower.store.domain.Store;
import com.flower.store.domain.StoreStatus;
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
        String name = "My Flower Shop";
        String address = "Seoul, Gangnam";
        Double lat = 37.5;
        Double lon = 127.0;

        Store savedStore = Store.builder()
                .ownerId(memberId)
                .name(name)
                .address(address)
                .lat(lat)
                .lon(lon)
                .status(StoreStatus.PENDING)
                .build();
        
        // Mocking the behavior to return a store with an ID (simulate saving)
        // Reflection or just assuming the returned object is what we want to check
        // Since Store.id is generated, we can't easily set it without reflection or a setter (which we avoided)
        // But for this test, we just want to verify save is called and result is handled.
        // Actually, let's mock the return value.
        
        given(storeRepository.save(any(Store.class))).willReturn(savedStore);

        // when
        storeService.registerStore(memberId, name, address, lat, lon);

        // then
        verify(storeRepository).save(any(Store.class));
    }
}
