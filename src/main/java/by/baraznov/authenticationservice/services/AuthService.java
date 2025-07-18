package by.baraznov.authenticationservice.services;

import by.baraznov.authenticationservice.dtos.RequestDTO;
import by.baraznov.authenticationservice.dtos.ResponseDTO;
import by.baraznov.authenticationservice.dtos.ValidDTO;
import by.baraznov.authenticationservice.models.JwtAuthentication;
import by.baraznov.authenticationservice.models.User;
import by.baraznov.authenticationservice.securities.JwtProvider;
import by.baraznov.authenticationservice.utils.JwtValidationException;
import by.baraznov.authenticationservice.utils.PasswordException;
import by.baraznov.authenticationservice.utils.UserNotFoundException;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public ResponseDTO login(RequestDTO authRequest) {
        User user = userService.getUserByLogin(authRequest.login())
                .orElseThrow(() -> new UserNotFoundException("User with login " +
                        authRequest.login() + " doesn't exist"));
        if (passwordEncoder.matches(authRequest.password(), user.getPassword())) {
            String accessToken = jwtProvider.generateAccessToken(user);
            String refreshToken = jwtProvider.generateRefreshToken(user);
            return new ResponseDTO(accessToken, refreshToken);
        } else {
            throw new PasswordException("Wrong password");
        }
    }

    public ValidDTO validAccessToken(String accessToken) {
        if (StringUtils.hasText(accessToken) && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }
        if (jwtProvider.validateAccessToken(accessToken)) {
            return new ValidDTO("Access token is valid");
        } else {
            throw new JwtValidationException("Invalid access token");
        }
    }

    public ResponseDTO refreshTokens(String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            Integer id = Integer.valueOf(claims.getSubject());
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new UserNotFoundException("User doesn't exist"));
            String accessToken = jwtProvider.generateAccessToken(user);
            String newRefreshToken = jwtProvider.generateRefreshToken(user);
            return new ResponseDTO(accessToken, newRefreshToken);
        }
        throw new JwtValidationException("Invalid JWT token, login to continue");
    }

    public ResponseDTO registration(RequestDTO authRequest) {
        User user = User.builder()
                .login(authRequest.login())
                .password(passwordEncoder.encode(authRequest.password()))
                .build();
        userService.create(user);
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);
        return new ResponseDTO(accessToken, refreshToken);
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

}