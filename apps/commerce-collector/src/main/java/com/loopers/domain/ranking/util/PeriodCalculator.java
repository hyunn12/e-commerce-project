package com.loopers.domain.ranking.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;

@Component
public class PeriodCalculator {

    private static final DateTimeFormatter YEAR_WEEK_FORMATTER = DateTimeFormatter.ofPattern("yyyy-'W'ww");
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    public String getYearWeek(LocalDate date) {
        int year = date.getYear();
        int weekOfYear = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        return String.format("%d-W%02d", year, weekOfYear);
    }

    public String getYearMonth(LocalDate date) {
        return date.format(YEAR_MONTH_FORMATTER);
    }

    public LocalDate getWeekStartDate(String yearWeek) {
        // 2025-W36 형식 파싱
        String[] parts = yearWeek.split("-W");
        int year = Integer.parseInt(parts[0]);
        int week = Integer.parseInt(parts[1]);

        return LocalDate.of(year, 1, 1)
                .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                .with(ChronoField.DAY_OF_WEEK, 1
                ); // 월요일
    }

    public LocalDate getWeekEndDate(String yearWeek) {
        return getWeekStartDate(yearWeek).plusDays(6);
    }

    public LocalDate getMonthStartDate(String yearMonth) {
        int year = Integer.parseInt(yearMonth.substring(0, 4));
        int month = Integer.parseInt(yearMonth.substring(4, 6));
        return LocalDate.of(year, month, 1);
    }

    public LocalDate getMonthEndDate(String yearMonth) {
        LocalDate startDate = getMonthStartDate(yearMonth);
        return startDate.plusMonths(1).minusDays(1);
    }
}
