package exception;

public class ManagerValidateException extends RuntimeException {
    public ManagerValidateException(final String message) {
        super(message);
    }
}
