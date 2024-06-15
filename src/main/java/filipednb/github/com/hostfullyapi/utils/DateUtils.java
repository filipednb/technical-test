package filipednb.github.com.hostfullyapi.utils;

import filipednb.github.com.hostfullyapi.exception.InvalidDateRangeException;

import java.time.Duration;
import java.time.LocalDateTime;

public class DateUtils {

    public static void validateDateRange(final LocalDateTime startDate,
                                         final LocalDateTime endDate,
                                         final Integer minHoursDuration) {
        var actualTime = TimeProvider.now();

        if (startDate == null || endDate == null) {
           throw new InvalidDateRangeException("Initial and final dates are mandatory");
        }

        if (startDate.isBefore(actualTime)) {
            throw new InvalidDateRangeException("Initial date should be in the present or in the future");
        }

        if (endDate.isBefore(actualTime)) {
            throw new InvalidDateRangeException("Final date should be in the future");
        }

        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("Initial date must be before the end date");
        }

        Duration duration = Duration.between(startDate, endDate);
        if (duration.toHours() < minHoursDuration) {
            throw new InvalidDateRangeException(
                    String.format("Duration should have at least %d hours", minHoursDuration));
        }
    }
}
