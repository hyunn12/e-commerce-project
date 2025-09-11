package com.loopers.domain.metrics;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ranking.weight")
public class ProductRankingWeightProperties {
    private double view;
    private double like;
    private double sales;
}
