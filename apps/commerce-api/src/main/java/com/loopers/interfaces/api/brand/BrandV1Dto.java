package com.loopers.interfaces.api.brand;

import com.loopers.application.brand.BrandCommand;
import com.loopers.application.brand.BrandInfo;

public class BrandV1Dto {

    public record BrandRequest(
            Long id,
            String name,
            String description
    ) {
        public BrandCommand toCommand() {
            return BrandCommand.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .build();
        }
    }

    public record BrandResponse(
            Long id,
            String name,
            String description
    ) {
        public static BrandResponse from(BrandInfo info) {
            return new BrandResponse(
                    info.getId(),
                    info.getName(),
                    info.getDescription()
            );
        }
    }
}
