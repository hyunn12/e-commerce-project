package com.loopers.batch.job;

import com.loopers.batch.dto.ProductRankingAggregateResult;
import com.loopers.batch.processor.MonthlyRankingProcessor;
import com.loopers.batch.writer.MonthlyRankingWriter;
import com.loopers.domain.ranking.ProductMonthlyRanking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MonthlyRankingJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RepositoryItemReader<ProductRankingAggregateResult> monthlyRankingReader;
    private final MonthlyRankingProcessor monthlyRankingProcessor;
    private final MonthlyRankingWriter monthlyRankingWriter;

    @Bean
    public Job monthlyRankingJob() {
        return new JobBuilder("monthlyRankingJob", jobRepository)
                .start(aggregateStep())
                .build();
    }

    @Bean
    public Step aggregateStep() {
        return new StepBuilder("aggregateStep", jobRepository)
                .<ProductRankingAggregateResult, ProductMonthlyRanking>chunk(1000, transactionManager)
                .reader(monthlyRankingReader)
                .processor(monthlyRankingProcessor)
                .writer(monthlyRankingWriter)
                .build();
    }
}
