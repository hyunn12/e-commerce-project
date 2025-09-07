package com.loopers.domain.event.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrandModifyEvent {

    private Long brandId;

    public static BrandModifyEvent of(Long brandId) {
        return new BrandModifyEvent(brandId);
    }
}
