package econest.client.exception;

// every exception in the client part extends this one
public class ClientException extends Exception {
    private static final long serialVersionUID = 1L;

    public ClientException(String message) { super(message); }
    public ClientException(String message, Throwable cause) { super(message, cause); }
}
