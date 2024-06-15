package filipednb.github.com.hostfullyapi.util;

import filipednb.github.com.hostfullyapi.exception.InvalidDateRangeException;
import filipednb.github.com.hostfullyapi.utils.DateUtils;
import filipednb.github.com.hostfullyapi.utils.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DateUtilsTest {

    private static final int MIN_HOURS_DURATION = 24;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.of(2024, 6, 17, 0, 0);
    }

    @Test
    void testValidateDateRange_NullDates() {
        try (MockedStatic<TimeProvider> mockedTimeProvider = Mockito.mockStatic(TimeProvider.class)) {
            mockedTimeProvider.when(TimeProvider::now).thenReturn(now);

            InvalidDateRangeException exception;

            exception = assertThrows(InvalidDateRangeException.class, () ->
                    DateUtils.validateDateRange(null, now.plusDays(1), MIN_HOURS_DURATION));
            assertEquals("Initial and final dates are mandatory", exception.getMessage());

            exception = assertThrows(InvalidDateRangeException.class, () ->
                    DateUtils.validateDateRange(now, null, MIN_HOURS_DURATION));
            assertEquals("Initial and final dates are mandatory", exception.getMessage());

            exception = assertThrows(InvalidDateRangeException.class, () ->
                    DateUtils.validateDateRange(null, null, MIN_HOURS_DURATION));
            assertEquals("Initial and final dates are mandatory", exception.getMessage());
        }
    }

    @Test
    void testValidateDateRange_StartDateBeforeNow() {
        try (MockedStatic<TimeProvider> mockedTimeProvider = Mockito.mockStatic(TimeProvider.class)) {
            mockedTimeProvider.when(TimeProvider::now).thenReturn(now);

            InvalidDateRangeException exception = assertThrows(InvalidDateRangeException.class, () ->
                    DateUtils.validateDateRange(now.minusDays(1), now.plusDays(1), MIN_HOURS_DURATION));
            assertEquals("Initial date should be in the present or in the future", exception.getMessage());
        }
    }

    @Test
    void testValidateDateRange_EndDateBeforeNow() {
        try (MockedStatic<TimeProvider> mockedTimeProvider = Mockito.mockStatic(TimeProvider.class)) {
            mockedTimeProvider.when(TimeProvider::now).thenReturn(now);

            InvalidDateRangeException exception = assertThrows(InvalidDateRangeException.class, () ->
                    DateUtils.validateDateRange(now, now.minusDays(1), MIN_HOURS_DURATION));
            assertEquals("Final date should be in the future", exception.getMessage());
        }
    }

    @Test
    void testValidateDateRange_StartDateAfterEndDate() {
        try (MockedStatic<TimeProvider> mockedTimeProvider = Mockito.mockStatic(TimeProvider.class)) {
            mockedTimeProvider.when(TimeProvider::now).thenReturn(now);

            InvalidDateRangeException exception = assertThrows(InvalidDateRangeException.class, () ->
                    DateUtils.validateDateRange(now.plusDays(2), now.plusDays(1), MIN_HOURS_DURATION));
            assertEquals("Initial date must be before the end date", exception.getMessage());
        }
    }

    @Test
    void testValidateDateRange_DurationTooShort() {
        try (MockedStatic<TimeProvider> mockedTimeProvider = Mockito.mockStatic(TimeProvider.class)) {
            mockedTimeProvider.when(TimeProvider::now).thenReturn(now);

            InvalidDateRangeException exception = assertThrows(InvalidDateRangeException.class, () ->
                    DateUtils.validateDateRange(now.plusHours(1), now.plusHours(2), MIN_HOURS_DURATION));
            assertEquals("Duration should have at least 24 hours", exception.getMessage());
        }
    }

    @Test
    void testValidateDateRange_Success() {
        try (MockedStatic<TimeProvider> mockedTimeProvider = Mockito.mockStatic(TimeProvider.class)) {
            mockedTimeProvider.when(TimeProvider::now).thenReturn(now);

            DateUtils.validateDateRange(now.plusHours(1), now.plusHours(25), MIN_HOURS_DURATION);
        }
    }
}