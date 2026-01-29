package com.flower.curation.repository;

import com.flower.curation.domain.SeasonalFlower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeasonalFlowerRepository extends JpaRepository<SeasonalFlower, Long> {
    
    List<SeasonalFlower> findByMonth(Integer month);
    
    List<SeasonalFlower> findByMonthAndPeakSeason(Integer month, Boolean peakSeason);
}
