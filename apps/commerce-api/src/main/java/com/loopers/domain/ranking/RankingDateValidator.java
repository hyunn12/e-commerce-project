package com.loopers.domain.ranking;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.util.Locale;

@Component
public class RankingDateValidator {

    private static final DateTimeFormatter DAILY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTHLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public void validateDailyDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "날짜는 필수입니다.");
        }

        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 날짜 형식입니다. 형식: YYYY-MM-DD");
        }

        try {
            LocalDate parsedDate = LocalDate.parse(date, DAILY_FORMATTER);
            
            if (parsedDate.isAfter(LocalDate.now())) {
                throw new CoreException(ErrorType.BAD_REQUEST, "미래 날짜는 조회할 수 없습니다: " + date);
            }
        } catch (DateTimeParseException e) {
            throw new CoreException(ErrorType.BAD_REQUEST, "존재하지 않는 날짜입니다: " + date);
        }
    }

    public void validateWeeklyDate(String yearWeek) {
        if (yearWeek == null || yearWeek.trim().isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주차는 필수입니다.");
        }

        if (!yearWeek.matches("\\d{4}-W\\d{2}")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 주차 형식입니다. 형식: YYYY-W##");
        }

        try {
            String[] parts = yearWeek.split("-W");
            int year = Integer.parseInt(parts[0]);
            int week = Integer.parseInt(parts[1]);

            if (week < 1 || week > 53) {
                throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 주차입니다. 주차는 1-53 사이여야 합니다: " + week);
            }

            LocalDate jan1 = LocalDate.of(year, 1, 1);
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int maxWeekOfYear = jan1.with(weekFields.dayOfWeek(), 7)
                    .with(weekFields.weekOfWeekBasedYear(), jan1.get(weekFields.weekOfWeekBasedYear()))
                    .plusWeeks(51)
                    .get(weekFields.weekOfWeekBasedYear());

            if (week > maxWeekOfYear) {
                throw new CoreException(ErrorType.BAD_REQUEST, year + "년에는 " + week + "주차가 존재하지 않습니다.");
            }

            LocalDate currentDate = LocalDate.now();
            int currentYear = currentDate.get(weekFields.weekBasedYear());
            int currentWeek = currentDate.get(weekFields.weekOfWeekBasedYear());

            if (year > currentYear || (year == currentYear && week > currentWeek)) {
                throw new CoreException(ErrorType.BAD_REQUEST, "미래 주차는 조회할 수 없습니다: " + yearWeek);
            }

        } catch (NumberFormatException e) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 주차 형식입니다: " + yearWeek);
        }
    }

    public void validateMonthlyDate(String yearMonth) {
        if (yearMonth == null || yearMonth.trim().isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "년월은 필수입니다.");
        }

        if (!yearMonth.matches("\\d{4}-\\d{2}")) {
            throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 년월 형식입니다. 형식: YYYY-MM");
        }

        try {
            YearMonth parsedYearMonth = YearMonth.parse(yearMonth, MONTHLY_FORMATTER);
            
            YearMonth currentYearMonth = YearMonth.now();
            if (parsedYearMonth.isAfter(currentYearMonth)) {
                throw new CoreException(ErrorType.BAD_REQUEST, "미래 월은 조회할 수 없습니다: " + yearMonth);
            }
            
            if (parsedYearMonth.isBefore(currentYearMonth.minusYears(2))) {
                throw new CoreException(ErrorType.BAD_REQUEST, "2년 이전 월은 조회할 수 없습니다: " + yearMonth);
            }
            
        } catch (DateTimeParseException e) {
            throw new CoreException(ErrorType.BAD_REQUEST, "존재하지 않는 년월입니다: " + yearMonth);
        }
    }
}
