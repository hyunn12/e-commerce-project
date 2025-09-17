package com.loopers.batch.reader;

import com.loopers.batch.dto.ProductRankingAggregateResult;
import com.loopers.infrastructure.ProductMetricsJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MonthlyRankingReader {

    private final ProductMetricsJpaRepository productMetricsJpaRepository;

    @Bean
    @StepScope
    public RepositoryItemReader<ProductRankingAggregateResult> monthlyRankingReader(
            @Value("#{jobParameters['startDate']}") String startDateParam,
            @Value("#{jobParameters['endDate']}") String endDateParam
    ) {

        LocalDate startDate = LocalDate.parse(startDateParam);
        LocalDate endDate = LocalDate.parse(endDateParam);
        ZonedDateTime startDateTime = startDate.atStartOfDay().atZone(java.time.ZoneId.systemDefault());
        ZonedDateTime endDateTime = endDate.plusDays(1).atStartOfDay().atZone(java.time.ZoneId.systemDefault());

        RepositoryItemReader<ProductRankingAggregateResult> reader = new RepositoryItemReader<>();
        reader.setRepository(productMetricsJpaRepository);
        reader.setMethodName("findAggregatedByUpdatedAtBetween");
        reader.setArguments(Arrays.asList(startDateTime, endDateTime));
        reader.setPageSize(1000);
        reader.setSaveState(false);
        reader.setName("monthlyRankingReader");

        // 정렬
        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("productId", Sort.Direction.ASC);
        reader.setSort(sorts);

        return reader;
    }
}
