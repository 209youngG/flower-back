package com.flower.curation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "seasonal_flowers", indexes = {
        @Index(name = "idx_month", columnList = "month")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeasonalFlower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "\"MONTH\"", nullable = false)
    private Integer month;

    @Column(nullable = false, length = 100)
    private String flowerName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean peakSeason = false;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder
    public SeasonalFlower(Integer month, String flowerName, String description, Boolean peakSeason) {
        this.month = month;
        this.flowerName = flowerName;
        this.description = description;
        this.peakSeason = peakSeason != null ? peakSeason : false;
        this.createdAt = LocalDateTime.now();
    }
}
