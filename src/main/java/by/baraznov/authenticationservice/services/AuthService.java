package by.baraznov.authenticationservice.services;

import by.baraznov.authenticationservice.dtos.RequestDTO;
import by.baraznov.authenticationservice.dtos.ResponseDTO;
import by.baraznov.authenticationservice.models.User;
import by.baraznov.authenticationservice.securities.JwtProvider;
import by.baraznov.authenticationservice.utils.AuthException;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    public ResponseDTO login(RequestDTO authRequest) {
        User user = userService.getUserByLogin(authRequest.login())
                .orElseThrow(() -> new AuthException("User with login " +
                        authRequest.login() + " doesn't exist"));
        if (user.getPassword().equals(authRequest.password())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            return new ResponseDTO(accessToken, refreshToken);
        } else {
            throw new AuthException("Wrong password");
        }
    }

    public ResponseDTO getAccessToken(String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            String login = claims.getSubject();
            User user = userService.getUserByLogin(login)
                    .orElseThrow(() -> new AuthException("User doesn't exist"));
            String accessToken = jwtProvider.generateAccessToken(user);
            return new ResponseDTO(accessToken, null);

        }
        return new ResponseDTO(null, null);
    }

    public ResponseDTO refresh(String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            String login = claims.getSubject();
            User user = userService.getUserByLogin(login)
                    .orElseThrow(() -> new AuthException("User doesn't exist"));
            String accessToken = jwtProvider.generateAccessToken(user);
            String newRefreshToken = jwtProvider.generateRefreshToken(user);
            return new ResponseDTO(accessToken, newRefreshToken);
        }
        throw new AuthException("Invalid JWT token");
    }

/*    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }*/

}