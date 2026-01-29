package com.flower.curation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Vibe {
    LOVELY("사랑스러운", "#FFB6C1"),
    VIVID("생동감 있는", "#FF6347"),
    CHIC("세련된", "#2F4F4F"),
    NATURAL("자연스러운", "#90EE90");

    private final String description;
    private final String colorCode;

    public boolean matchesColor(String productColor) {
        if (productColor == null) return false;
        
        String lowerColor = productColor.toLowerCase();
        return switch (this) {
            case LOVELY -> lowerColor.contains("핑크") || lowerColor.contains("분홍") 
                        || lowerColor.contains("pink") || lowerColor.contains("로즈");
            case VIVID -> lowerColor.contains("빨강") || lowerColor.contains("주황") 
                       || lowerColor.contains("red") || lowerColor.contains("orange");
            case CHIC -> lowerColor.contains("검정") || lowerColor.contains("다크") 
                      || lowerColor.contains("black") || lowerColor.contains("그린");
            case NATURAL -> lowerColor.contains("화이트") || lowerColor.contains("흰") 
                         || lowerColor.contains("white") || lowerColor.contains("그린");
        };
    }
}
