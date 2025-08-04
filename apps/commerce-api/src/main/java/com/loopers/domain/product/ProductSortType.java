package com.loopers.domain.product;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ProductSortType {
    LATEST("latest"),
    PRICE_ASC("price_asc"),
    LIKES_DESC("likes_desc");

    private final String value;

    ProductSortType(String value) {
        this.value = value;
    }

    public static ProductSortType from(String value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 정렬 기준입니다: " + value));
    }
}
