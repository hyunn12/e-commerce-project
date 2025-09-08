package com.loopers.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductMetricsService {

    private final ProductMetricsRepository productMetricsRepository;

    @Transactional
    public void bulkUpdate(Map<Long, ProductMetricsCount> aggregate) {
        aggregate.forEach(productMetricsRepository::upsert);
    }
}
