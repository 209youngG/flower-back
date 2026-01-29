package com.flower.curation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Occasion {
    CONFESSION("고백"),
    BIRTHDAY("생일"),
    ANNIVERSARY("기념일"),
    COMFORT("위로"),
    CONGRATULATION("축하/승진"),
    GRATITUDE("감사"),
    APOLOGY("사과"),
    GET_WELL("쾌유");

    private final String description;
}
