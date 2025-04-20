package eeet2580.kunlun.opwa.backend.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import eeet2580.kunlun.opwa.backend.common.dto.resp.BaseRes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        BaseRes<String> errorResponse = new BaseRes<>(
                HttpStatus.UNAUTHORIZED.value(),
                "Authentication required",
                null
        );

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}