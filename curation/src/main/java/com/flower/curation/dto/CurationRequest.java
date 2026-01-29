package com.flower.curation.dto;

import com.flower.curation.enums.Vibe;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CurationRequest(
        @NotNull String who,
        @NotNull @Size(min = 1) List<String> why,
        @NotNull Vibe vibe,
        @NotNull BigDecimal budget,
        String preferredColor
) {
}
