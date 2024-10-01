package exception;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(final String message) {
        super(message);
    }

    public ManagerSaveException(final String message, Exception e) {
        super(message, e);
    }
}
