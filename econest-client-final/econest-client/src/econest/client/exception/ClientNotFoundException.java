package econest.client.exception;

// no client with that id
public class ClientNotFoundException extends ClientException {
    private static final long serialVersionUID = 1L;

    public ClientNotFoundException(String message) { super(message); }
    public ClientNotFoundException(String message, Throwable cause) { super(message, cause); }
}
