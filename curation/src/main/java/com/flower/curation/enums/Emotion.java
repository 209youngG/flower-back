package com.flower.curation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Emotion {
    LOVE("사랑"),
    RESPECT("존경"),
    SYMPATHY("동정/연민"),
    JOY("기쁨"),
    HOPE("희망");

    private final String description;
}
