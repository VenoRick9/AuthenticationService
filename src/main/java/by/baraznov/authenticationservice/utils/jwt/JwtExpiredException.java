package by.baraznov.authenticationservice.utils.jwt;

public class JwtExpiredException extends RuntimeException {
    public JwtExpiredException(String message) {
        super(message);
    }
}
