package by.baraznov.authenticationservice.controllers;

import by.baraznov.authenticationservice.dtos.RefreshJwtRequestDTO;
import by.baraznov.authenticationservice.dtos.RequestDTO;
import by.baraznov.authenticationservice.dtos.ResponseDTO;
import by.baraznov.authenticationservice.dtos.ValidDTO;
import by.baraznov.authenticationservice.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody RequestDTO authRequest) {
        ResponseDTO token = authService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/valid")
    public ResponseEntity<ValidDTO> validate(@RequestHeader("Authorization") String accessToken) {
        ValidDTO response = authService.validAccessToken(accessToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseDTO> refreshToken(@RequestBody RefreshJwtRequestDTO request) {
        ResponseDTO token = authService.refreshTokens(request.refreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/registration")
    public ResponseEntity<ResponseDTO> registration(@RequestBody RequestDTO authRequest) {
        ResponseDTO token = authService.register(authRequest);
        return ResponseEntity.ok(token);
    }
}