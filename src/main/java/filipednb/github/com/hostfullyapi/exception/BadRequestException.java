package filipednb.github.com.hostfullyapi.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(final String msg) {
        super(msg);
    }
}
