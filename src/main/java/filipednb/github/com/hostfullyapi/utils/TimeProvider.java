package filipednb.github.com.hostfullyapi.utils;

import java.time.LocalDateTime;

public class TimeProvider {

    private static final ThreadLocal<LocalDateTime> MOCK_TIME = new ThreadLocal<>();

    public static LocalDateTime now() {
        return MOCK_TIME.get() != null ? MOCK_TIME.get() : LocalDateTime.now();
    }

    public static void setMockTime(LocalDateTime mockTime) {
        MOCK_TIME.set(mockTime);
    }

    public static void resetMockTime() {
        MOCK_TIME.remove();
    }
}