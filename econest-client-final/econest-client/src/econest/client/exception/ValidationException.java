package econest.client.exception;

// bad user input (wrong email, short name etc.)
public class ValidationException extends ClientException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) { super(message); }
    public ValidationException(String message, Throwable cause) { super(message, cause); }
}
