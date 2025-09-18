package com.loopers.application;

import com.loopers.domain.ranking.util.PeriodCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingJobService {

    private final JobLauncher jobLauncher;
    private final Job weeklyRankingJob;
    private final Job monthlyRankingJob;
    private final PeriodCalculator periodCalculator;

    public void runWeeklyJob() {
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            String yearWeek = periodCalculator.getYearWeek(yesterday);

            runWeeklyJobWithYearWeek(yearWeek);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            throw new IllegalStateException("Error Weekly Product Ranking Job", e);
        }
    }

    private void runWeeklyJobWithYearWeek(String yearWeek) throws Exception {
        log.info("Starting Weekly Product Ranking Job: yearWeek={}", yearWeek);

        LocalDate startDate = periodCalculator.getWeekStartDate(yearWeek);
        LocalDate endDate = periodCalculator.getWeekEndDate(yearWeek);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("yearWeek", yearWeek)
                .addString("startDate", startDate.toString())
                .addString("endDate", endDate.toString())
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(weeklyRankingJob, jobParameters);

        if (jobExecution.getStatus().isUnsuccessful()) {
            log.error("Weekly Product Ranking Job Failed: yearWeek={}", yearWeek);
            jobExecution.getAllFailureExceptions().forEach(throwable ->
                    log.error("Job failure exception: ", throwable)
            );
        }
    }

    public void runMonthlyJob() {
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            String yearMonth = periodCalculator.getYearMonth(yesterday);

            runMonthlyJobWithYearMonth(yearMonth);
        } catch (Exception e) {
            log.error("Error Running Monthly Product Ranking Job", e);
            throw new IllegalStateException("Error Running Monthly Product Ranking Job", e);
        }
    }

    private void runMonthlyJobWithYearMonth(String yearMonth) throws Exception {
        LocalDate startDate = periodCalculator.getMonthStartDate(yearMonth);
        LocalDate endDate = periodCalculator.getMonthEndDate(yearMonth);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("yearMonth", yearMonth)
                .addString("startDate", startDate.toString())
                .addString("endDate", endDate.toString())
                .toJobParameters();

        JobExecution jobExecution = jobLauncher.run(monthlyRankingJob, jobParameters);

        if (jobExecution.getStatus().isUnsuccessful()) {
            log.error("Monthly Product Ranking Job Failed: yearWeek={}", yearMonth);
            jobExecution.getAllFailureExceptions().forEach(throwable ->
                    log.error("Job failure exception: ", throwable)
            );
        }
    }
}
