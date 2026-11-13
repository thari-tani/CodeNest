package econest.client.exception;

// wraps SQLException so the other layers don't need to import java.sql
public class DatabaseException extends ClientException {
    private static final long serialVersionUID = 1L;

    public DatabaseException(String message) { super(message); }
    public DatabaseException(String message, Throwable cause) { super(message, cause); }
}
