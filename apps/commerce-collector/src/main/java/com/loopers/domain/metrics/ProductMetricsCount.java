package com.loopers.domain.metrics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductMetricsCount {

    private int likeCount;
    private int viewCount;
    private int salesCount;

    public void apply(String type, int quantity) {
        switch (type) {
            case "LIKE_ADD" -> likeCount++;
            case "LIKE_DELETE" -> likeCount--;
            case "PRODUCT_VIEW" -> viewCount++;
            case "STOCK_INCREASE" -> salesCount += quantity;
            case "STOCK_DECREASE" -> salesCount -= quantity;
        }
    }
}
