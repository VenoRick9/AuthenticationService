package by.baraznov.authenticationservice.utils;

import by.baraznov.authenticationservice.models.JwtAuthentication;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {

    public static JwtAuthentication generate(Claims claims) {
        final JwtAuthentication jwtInfoToken = new JwtAuthentication();
        jwtInfoToken.setLogin(claims.get("login", String.class));
        jwtInfoToken.setLogin(claims.getSubject());
        return jwtInfoToken;
    }


}
