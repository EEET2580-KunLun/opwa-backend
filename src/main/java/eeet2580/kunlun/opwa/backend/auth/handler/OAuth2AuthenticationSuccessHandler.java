package eeet2580.kunlun.opwa.backend.auth.handler;

import eeet2580.kunlun.opwa.backend.auth.config.JwtTokenUtil;
import eeet2580.kunlun.opwa.backend.staff.model.StaffEntity;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    public OAuth2AuthenticationSuccessHandler(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        StaffEntity staff;
        if (authentication.getPrincipal() instanceof StaffEntity) {
            staff = (StaffEntity) authentication.getPrincipal();
        } else {
//            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
//            OAuth2User oAuth2User = oauthToken.getPrincipal();
            throw new ServletException("Unable to process OAuth2 login: principal is not a StaffEntity");
        }
        String token = jwtTokenUtil.generateToken(staff);

        // Set JWT in http-only cookie
        Cookie cookie = new Cookie("jwt_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // only works with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtTokenUtil.getExpiration() / 1000));
        response.addCookie(cookie);

//        String redirectUrl = UriComponentsBuilder.fromUriString(frontendBaseUrl + "/oauth2/callback")
//                .queryParam("token", token)
//                .build().toUriString();

        // Redirect to frontend after successful authentication
        String redirectUrl = frontendBaseUrl + "/dashboard";
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}