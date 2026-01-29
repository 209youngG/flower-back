package com.flower.api.controller;

import com.flower.curation.dto.CurationRequest;
import com.flower.curation.dto.CurationResult;
import com.flower.curation.service.CurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/curation")
@RequiredArgsConstructor
@Tag(name = "Curation", description = "AI 꽃 큐레이션 API - Flori Sommelier")
public class CurationController {

    private final CurationService curationService;

    @Operation(
            summary = "AI 꽃 추천",
            description = """
                    누구에게(who), 왜(why), 어떤 분위기(vibe), 예산(budget)을 입력하면
                    AI가 최적의 꽃을 추천합니다.
                    
                    - who: 받는 사람 (예: 연인, 부모님, 친구, 동료)
                    - why: 상황 (CONFESSION, BIRTHDAY, ANNIVERSARY, GRATITUDE, COMFORT, CONGRATULATION, APOLOGY, GET_WELL)
                    - vibe: 분위기 (LOVELY, VIVID, CHIC, NATURAL)
                    - budget: 예산 (숫자)
                    - preferredColor: 선호 색상 (선택사항)
                    
                    **응답:**
                    - bestSeller: 리뷰 많은 인기 상품 Top 3
                    - storytelling: 꽃말이 잘 어울리는 상품 Top 3
                    - smartChoice: 가성비 좋은 상품 Top 3
                    - flowerLanguages: 추천 꽃말 정보
                    - recommendationReason: AI가 생성한 추천 이유
                    """
    )
    @PostMapping("/recommend")
    public ResponseEntity<CurationResult> recommendFlowers(
            @Valid @RequestBody CurationRequest request
    ) {
        log.info("AI 꽃 추천 요청 - who: {}, why: {}, vibe: {}, budget: {}",
                request.who(), request.why(), request.vibe(), request.budget());

        CurationResult result = curationService.recommendFlowers(request);

        log.info("AI 꽃 추천 완료 - bestSeller: {}, storytelling: {}, smartChoice: {}",
                result.bestSeller().size(), result.storytelling().size(), result.smartChoice().size());

        return ResponseEntity.ok(result);
    }
}
