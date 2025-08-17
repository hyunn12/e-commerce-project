package com.loopers.application.brand;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BrandInfoList {

    private List<BrandInfo> brandInfos;

    public static BrandInfoList of(List<BrandInfo> brandInfos) {
        return new BrandInfoList(brandInfos);
    }
}
