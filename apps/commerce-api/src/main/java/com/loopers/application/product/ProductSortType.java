package com.loopers.application.product;

public enum ProductSortType {
    LATEST,
    PRICE_ASC,
    LIKES_DESC;

    public static ProductSortType from(String raw) {
        return switch (raw.toLowerCase()) {
            case "latest" -> LATEST;
            case "price_asc" -> PRICE_ASC;
            case "likes_desc" -> LIKES_DESC;
            default -> throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다.");
        };
    }
}

