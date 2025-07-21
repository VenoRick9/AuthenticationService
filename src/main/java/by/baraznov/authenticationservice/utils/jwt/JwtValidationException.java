package by.baraznov.authenticationservice.utils.jwt;

public class JwtValidationException extends RuntimeException {
    public JwtValidationException(String message) {
        super(message);
    }
}
