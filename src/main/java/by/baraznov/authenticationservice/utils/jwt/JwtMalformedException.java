package by.baraznov.authenticationservice.utils.jwt;

public class JwtMalformedException extends RuntimeException {
    public JwtMalformedException(String message) {
        super(message);
    }
}
