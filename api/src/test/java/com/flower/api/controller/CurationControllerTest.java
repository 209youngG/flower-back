package com.flower.api.controller;

import com.flower.curation.dto.CurationRequest;
import com.flower.curation.dto.CurationResult;
import com.flower.curation.enums.Vibe;
import com.flower.curation.service.CurationService;
import com.flower.product.dto.ProductDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("CurationController 단위 테스트")
class CurationControllerTest {

    @Test
    @DisplayName("POST /api/v1/curation/recommend - AI 꽃 추천 성공")
    void should_returnCurationResult_when_validRequest() {
        // given
        CurationService curationService = mock(CurationService.class);
        CurationController controller = new CurationController(curationService);

        CurationRequest request = new CurationRequest(
                "연인",
                List.of("CONFESSION"),
                Vibe.LOVELY,
                new BigDecimal("50000"),
                null
        );

        CurationResult mockResult = new CurationResult(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                "연인에게 CONFESSION 상황에 어울리는 사랑스러운 분위기의 꽃을 추천드립니다."
        );

        given(curationService.recommendFlowers(any(CurationRequest.class)))
                .willReturn(mockResult);

        // when
        ResponseEntity<CurationResult> response = controller.recommendFlowers(request);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().recommendationReason()).contains("연인", "CONFESSION");

        verify(curationService).recommendFlowers(any(CurationRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/curation/recommend - 다중 상황 테스트")
    void should_returnCurationResult_when_multipleOccasions() {
        // given
        CurationService curationService = mock(CurationService.class);
        CurationController controller = new CurationController(curationService);

        CurationRequest request = new CurationRequest(
                "부모님",
                List.of("BIRTHDAY", "GRATITUDE"),
                Vibe.NATURAL,
                new BigDecimal("30000"),
                null
        );

        CurationResult mockResult = new CurationResult(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                "부모님에게 BIRTHDAY, GRATITUDE 상황에 어울리는 자연스러운 분위기의 꽃을 추천드립니다."
        );

        given(curationService.recommendFlowers(any(CurationRequest.class)))
                .willReturn(mockResult);

        // when
        ResponseEntity<CurationResult> response = controller.recommendFlowers(request);

        // then
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().recommendationReason()).contains("부모님");

        verify(curationService).recommendFlowers(any(CurationRequest.class));
    }
}

