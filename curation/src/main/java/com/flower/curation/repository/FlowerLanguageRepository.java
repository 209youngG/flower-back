package com.flower.curation.repository;

import com.flower.curation.domain.FlowerLanguage;
import com.flower.curation.enums.Emotion;
import com.flower.curation.enums.Occasion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlowerLanguageRepository extends JpaRepository<FlowerLanguage, Long> {

    List<FlowerLanguage> findByOccasion(Occasion occasion);

    List<FlowerLanguage> findByFlowerName(String flowerName);

    List<FlowerLanguage> findByOccasionAndEmotion(Occasion occasion, Emotion emotion);
}
