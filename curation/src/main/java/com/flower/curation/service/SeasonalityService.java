package com.flower.curation.service;

import com.flower.curation.repository.SeasonalFlowerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeasonalityService {

    private final SeasonalFlowerRepository repository;

    public List<String> getSeasonalFlowers(int month) {
        if (month < 1 || month > 12) {
            log.warn("Invalid month requested: {}", month);
            return Collections.emptyList();
        }

        return repository.findByMonth(month).stream()
                .map(sf -> sf.getFlowerName())
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getCurrentSeasonalFlowers() {
        int currentMonth = LocalDate.now().getMonthValue();
        return getSeasonalFlowers(currentMonth);
    }

    public List<String> getPeakSeasonFlowers(int month) {
        if (month < 1 || month > 12) {
            log.warn("Invalid month requested for peak season: {}", month);
            return Collections.emptyList();
        }

        return repository.findByMonthAndPeakSeason(month, true).stream()
                .map(sf -> sf.getFlowerName())
                .distinct()
                .collect(Collectors.toList());
    }
}
