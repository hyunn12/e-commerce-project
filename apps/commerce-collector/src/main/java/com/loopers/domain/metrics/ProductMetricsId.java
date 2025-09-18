package com.loopers.domain.metrics;

import jakarta.persistence.Column;
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

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "metric_date")
    private LocalDate date;
}
