package com.flower.curation.domain;

import com.flower.curation.enums.Emotion;
import com.flower.curation.enums.Occasion;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "flower_languages", indexes = {
        @Index(name = "idx_occasion", columnList = "occasion"),
        @Index(name = "idx_flower_name", columnList = "flowerName")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FlowerLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String flowerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Occasion occasion;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String meaning;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Emotion emotion;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder
    public FlowerLanguage(String flowerName, Occasion occasion, String meaning, Emotion emotion, String description) {
        this.flowerName = flowerName;
        this.occasion = occasion;
        this.meaning = meaning;
        this.emotion = emotion;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
}
