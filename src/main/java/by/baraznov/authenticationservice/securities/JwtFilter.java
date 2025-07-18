package by.baraznov.authenticationservice.securities;

import by.baraznov.authenticationservice.models.JwtAuthentication;
import by.baraznov.authenticationservice.utils.ErrorResponse;
import by.baraznov.authenticationservice.utils.JwtExpiredException;
import by.baraznov.authenticationservice.utils.JwtMalformedException;
import by.baraznov.authenticationservice.utils.JwtSignatureException;
import by.baraznov.authenticationservice.utils.JwtUtils;
import by.baraznov.authenticationservice.utils.JwtValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private static final String AUTHORIZATION = "Authorization";

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        try {
            String token = getTokenFromRequest((HttpServletRequest) servletRequest);
            if (token != null && jwtProvider.validateAccessToken(token)) {
                Claims claims = jwtProvider.getAccessClaims(token);
                JwtAuthentication jwtInfoToken = JwtUtils.generate(claims);
                jwtInfoToken.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);
            }
            filterChain.doFilter(servletRequest, servletResponse);
        }catch (JwtExpiredException | JwtSignatureException
                | JwtMalformedException | JwtValidationException e){
            sendErrorResponse(servletResponse, e.getMessage());
        }

    }
    private void sendErrorResponse(ServletResponse response, String message) throws IOException {
        if(response instanceof HttpServletResponse httpServletResponse) {
            httpServletResponse.setContentType("application/json");
            ErrorResponse dto = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), message, LocalDateTime.now());
            String json = objectMapper.writeValueAsString(dto);
            httpServletResponse.getWriter().write(json);
        }
    }
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}