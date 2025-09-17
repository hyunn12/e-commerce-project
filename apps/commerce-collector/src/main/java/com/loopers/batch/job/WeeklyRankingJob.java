package com.loopers.batch.job;

import com.loopers.batch.dto.WeeklyRankingAggregateResult;
import com.loopers.batch.processor.WeeklyRankingProcessor;
import com.loopers.batch.writer.WeeklyRankingWriter;
import com.loopers.domain.ranking.ProductWeeklyRanking;
import com.loopers.domain.ranking.ProductWeeklyRankingRepository;
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
public class WeeklyRankingJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RepositoryItemReader<WeeklyRankingAggregateResult> weeklyRankingReader;
    private final WeeklyRankingProcessor weeklyRankingProcessor;
    private final WeeklyRankingWriter weeklyRankingWriter;
    private final ProductWeeklyRankingRepository weeklyRankingRepository;

    @Bean
    public Job weeklyRankingJob() {
        return new JobBuilder("weeklyRankingJob", jobRepository)
            .start(aggregateStep())
            .build();
    }

    @Bean
    public Step aggregateStep() {
        return new StepBuilder("aggregateStep", jobRepository)
            .<WeeklyRankingAggregateResult, ProductWeeklyRanking>chunk(1000, transactionManager)
            .reader(weeklyRankingReader)
            .processor(weeklyRankingProcessor)
            .writer(weeklyRankingWriter)
            .build();
    }
}
