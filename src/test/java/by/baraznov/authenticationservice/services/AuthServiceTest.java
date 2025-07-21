package by.baraznov.authenticationservice.services;

import by.baraznov.authenticationservice.dtos.RequestDTO;
import by.baraznov.authenticationservice.dtos.ResponseDTO;
import by.baraznov.authenticationservice.dtos.ValidDTO;
import by.baraznov.authenticationservice.models.User;
import by.baraznov.authenticationservice.securities.JwtProvider;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private PasswordEncoder passwordEncoder;


    @InjectMocks
    private AuthService authService;

    @Test
    void test_login() {
        RequestDTO request = new RequestDTO("user", "pass");
        User user = User.builder().login("user").password("encoded").build();

        when(userService.getUserByLogin("user")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "encoded")).thenReturn(true);
        when(jwtProvider.generateAccessToken(user)).thenReturn("access-token");
        when(jwtProvider.generateRefreshToken(user)).thenReturn("refresh-token");

        ResponseDTO response = authService.login(request);

        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
    }

    @Test
    void test_validAccessToken() {
        String token = "Bearer valid-token";
        when(jwtProvider.validateAccessToken("valid-token")).thenReturn(true);

        ValidDTO result = authService.validAccessToken(token);

        assertEquals("Access token is valid", result.message());
    }

    @Test
    void test_refreshTokens() {
        String refreshToken = "refresh-token";
        Claims claims = mock(Claims.class);
        User user = User.builder().id(1).login("user").build();

        when(jwtProvider.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtProvider.getRefreshClaims(refreshToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn("1");
        when(userService.getUserById(1)).thenReturn(Optional.of(user));
        when(jwtProvider.generateAccessToken(user)).thenReturn("new-access");
        when(jwtProvider.generateRefreshToken(user)).thenReturn("new-refresh");

        ResponseDTO response = authService.refreshTokens(refreshToken);

        verify(userService).getUserById(1);
        assertEquals("new-access", response.accessToken());
        assertEquals("new-refresh", response.refreshToken());
    }

    @Test
    void test_registration() {
        RequestDTO request = new RequestDTO("newuser", "12345");
        when(userService.existsByLogin("newuser")).thenReturn(false);
        when(passwordEncoder.encode("12345")).thenReturn("encoded");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        when(jwtProvider.generateAccessToken(any())).thenReturn("access");
        when(jwtProvider.generateRefreshToken(any())).thenReturn("refresh");

        ResponseDTO response = authService.registration(request);

        verify(userService).create(userCaptor.capture());
        assertEquals("newuser", userCaptor.getValue().getLogin());
        assertEquals("access", response.accessToken());
        assertEquals("refresh", response.refreshToken());
    }
}
