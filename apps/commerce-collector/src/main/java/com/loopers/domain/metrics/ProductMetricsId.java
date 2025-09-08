package com.loopers.domain.metrics;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ProductMetricsId implements Serializable {
    private Long productId;
    private LocalDate date;
}
