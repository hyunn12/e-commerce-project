package com.loopers.application.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfoList {

    private List<ProductInfo.Main> mains;

    public static ProductInfoList of(List<ProductInfo.Main> productInfos) {
        return new ProductInfoList(productInfos);
    }
}

