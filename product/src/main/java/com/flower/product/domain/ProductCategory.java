package com.flower.product.domain;

/**
 * 꽃 쇼핑몰 상품 카테고리 (꽃선물, 개업화분 등)
 */
public enum ProductCategory {
    FLOWER_BOUQUET("꽃다발", "Flower bouquets"),
    FLOWER_GIFT("꽃선물", "Flower gifts for special occasions"),
    OPENING_PLANT("개업화분", "Opening ceremony plants"),
    PROMOTION_APPOINTMENT("승진/취임", "Promotion and appointment gifts"),
    WEDDING_FUNERAL("결혼/장례", "Wedding and funeral flowers"),
    TREND_PICK("트렌드픽", "Trending products"),
    DIY_FLOWER_MARKET("DIY 꽃시장", "DIY flower market"),
    SUBSCRIPTION("정기구독", "Subscription packages"),
    SAME_DAY_DELIVERY("오늘도착", "Same-day delivery products");

    private final String displayName;
    private final String description;

    ProductCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
