package com.flower.curation.service;

import com.flower.curation.domain.FlowerLanguage;
import com.flower.curation.dto.FlowerLanguageDto;
import com.flower.curation.enums.Emotion;
import com.flower.curation.enums.Occasion;
import com.flower.curation.repository.FlowerLanguageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FlowerLanguageService {

    private final FlowerLanguageRepository repository;

    public List<FlowerLanguageDto> findByOccasion(Occasion occasion) {
        return repository.findByOccasion(occasion).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<FlowerLanguageDto> findByFlowerName(String flowerName) {
        return repository.findByFlowerName(flowerName).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<FlowerLanguageDto> findByOccasionAndEmotion(Occasion occasion, String emotionStr) {
        Emotion emotion = Emotion.valueOf(emotionStr);
        return repository.findByOccasionAndEmotion(occasion, emotion).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private FlowerLanguageDto toDto(FlowerLanguage entity) {
        return new FlowerLanguageDto(
                entity.getId(),
                entity.getFlowerName(),
                entity.getOccasion().name(),
                entity.getMeaning(),
                entity.getEmotion() != null ? entity.getEmotion().name() : null,
                entity.getDescription()
        );
    }
}
