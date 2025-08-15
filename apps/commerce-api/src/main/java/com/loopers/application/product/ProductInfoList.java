package com.loopers.application.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfoList implements Serializable {

    private List<ProductInfo.Main> mains;

    public static ProductInfoList of(List<ProductInfo.Main> productInfos) {
        return new ProductInfoList(productInfos);
    }
}

