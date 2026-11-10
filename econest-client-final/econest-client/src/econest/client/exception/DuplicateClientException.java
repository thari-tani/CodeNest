package econest.client.exception;

// email or reg no is already used by someone else
public class DuplicateClientException extends ClientException {
    private static final long serialVersionUID = 1L;

    public DuplicateClientException(String message) { super(message); }
    public DuplicateClientException(String message, Throwable cause) { super(message, cause); }
}
