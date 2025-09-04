package com.loopers.application.brand;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrandCommand {

    private Long id;
    private String name;
    private String description;
}
