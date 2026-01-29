package com.flower.product.domain;

public enum ProductSize {
    S("Small", "작은 사이즈"),
    M("Medium", "중간 사이즈"),
    L("Large", "큰 사이즈"),
    XL("Extra Large", "특대 사이즈");

    private final String displayName;
    private final String description;

    ProductSize(String displayName, String description) {
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
