package by.baraznov.authenticationservice.securities;

import by.baraznov.authenticationservice.models.User;
import by.baraznov.authenticationservice.utils.jwt.JwtExpiredException;
import by.baraznov.authenticationservice.utils.jwt.JwtMalformedException;
import by.baraznov.authenticationservice.utils.jwt.JwtSignatureException;
import by.baraznov.authenticationservice.utils.jwt.JwtValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    private final String accessSecret = Base64.getEncoder().encodeToString("access-secret-key".repeat(5).getBytes());
    private final String refreshSecret = Base64.getEncoder().encodeToString("refresh-secret-key".repeat(5).getBytes());

    private final User testUser = User.builder()
            .id(1)
            .login("testuser")
            .password("encoded-password")
            .build();

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(accessSecret, refreshSecret);
    }

    @Test
    void test_generateAccessToken_and_validate() {
        String token = jwtProvider.generateAccessToken(testUser);
        assertNotNull(token);
        assertTrue(jwtProvider.validateAccessToken(token));

        Claims claims = jwtProvider.getAccessClaims(token);
        assertEquals("1", claims.getSubject());
        assertEquals("testuser", claims.get("login"));
    }

    @Test
    void test_generateRefreshToken_and_validate() {
        String token = jwtProvider.generateRefreshToken(testUser);
        assertNotNull(token);
        assertTrue(jwtProvider.validateRefreshToken(token));

        Claims claims = jwtProvider.getRefreshClaims(token);
        assertEquals("1", claims.getSubject());
    }

    @Test
    void test_invalidSignature() {
        JwtProvider otherProvider = new JwtProvider(
                Base64.getEncoder().encodeToString("another-access-secret-key-1234567890".getBytes()),
                refreshSecret
        );
        String token = otherProvider.generateAccessToken(testUser);

        JwtSignatureException ex = assertThrows(JwtSignatureException.class, () -> {
            jwtProvider.validateAccessToken(token);
        });
        assertEquals("Token signature is invalid", ex.getMessage());
    }

    @Test
    void test_malformedToken() {
        String malformed = "this.is.not.jwt";

        JwtMalformedException ex = assertThrows(JwtMalformedException.class, () -> {
            jwtProvider.validateAccessToken(malformed);
        });
        assertEquals("Malformed JWT token", ex.getMessage());
    }

    @Test
    void test_emptyToken() {
        JwtValidationException ex = assertThrows(JwtValidationException.class, () -> {
            jwtProvider.validateAccessToken("");
        });
        assertEquals("Invalid JWT token", ex.getMessage());
    }
    @Test
    void test_expiredToken_throwsException() {
        JwtProvider shortLivedProvider = new JwtProvider(accessSecret, refreshSecret);

        String expiredToken = Jwts.builder()
                .setSubject("1")
                .setExpiration(Date.from(Instant.now().minusSeconds(5)))
                .claim("login", "testuser")
                .signWith(Keys.hmacShaKeyFor(Base64.getDecoder().decode(accessSecret)))
                .compact();

        JwtExpiredException ex = assertThrows(JwtExpiredException.class, () -> {
            shortLivedProvider.validateAccessToken(expiredToken);
        });
        assertEquals("Expired JWT token", ex.getMessage());
    }
}
