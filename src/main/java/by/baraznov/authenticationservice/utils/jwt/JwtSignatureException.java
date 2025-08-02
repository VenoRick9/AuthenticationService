package by.baraznov.authenticationservice.utils.jwt;

public class JwtSignatureException extends RuntimeException {
    public JwtSignatureException(String message) {
        super(message);
    }
}
