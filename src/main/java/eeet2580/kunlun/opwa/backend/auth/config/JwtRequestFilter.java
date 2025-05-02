package eeet2580.kunlun.opwa.backend.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    public JwtRequestFilter(UserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain)

            throws ServletException, IOException {

        final String requestPath = request.getServletPath();

        // Extract JWT from cookies by try-catch to debug
        try {
            String jwtToken = extractCookieValue(request, "jwt_token");

            if (jwtToken != null) {
                try {
                    // Process the Jwt token
                    processJwtToken(jwtToken, request);
                    logger.debug("Successfully processed jwt token");
                } catch (ExpiredJwtException e) {
                    logger.debug("Jwt token expired: " + e.getMessage());
                    handleExpiredToken(request, response, e);
                } catch (SignatureException e) {
                    logger.error("Invalid JWT signature: " + e.getMessage());
                } catch (MalformedJwtException e) {
                    logger.error("Invalid JWT token: " + e.getMessage());
                } catch (Exception e) {
                    logger.error("Error processing JWT token: " + e.getMessage());
                }
            } else {
                logger.debug("No JWT token found in request");
            }
        } catch (Exception e) {
            logger.error("Failed to process authentication: " + e.getMessage());
        }

//        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) { // for using the Bearer token in the authorization header
//            jwtToken = requestTokenHeader.substring(7);
//            try {
//                Claims claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);
//                email = claims.getSubject();
//                String role = claims.get("role", String.class);
//
//                if (email != null && role != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
//
//                    if (!jwtTokenUtil.isTokenExpired(jwtToken)) {
//                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
//                                userDetails,
//                                null,
//                                List.of(new SimpleGrantedAuthority("ROLE_" + role)));
//
//                        usernamePasswordAuthenticationToken
//                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//                    }
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                // System.out.println("Unable to get JWT Token or JWT Token has expired");
//            }
//        }

        chain.doFilter(request, response);
    }

    private void processJwtToken(String jwtToken, HttpServletRequest request) throws Exception {

        Claims claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);
        String email = claims.getSubject();
        String role = claims.get("role", String.class);

        logger.debug("Processing JWT token for user: " + email + " with role: " + role);

        if (email != null && role != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Check if the token is expired
            if (jwtTokenUtil.isTokenExpired(jwtToken)) {
                throw new ExpiredJwtException(null, null, "Token expired");
            }

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role)));

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            logger.debug("Successfully set authentication for user: " + email);
        }
    }

    // The frontend will handle the logic of the refresh process
    private void handleExpiredToken(HttpServletRequest request, HttpServletResponse response, ExpiredJwtException e) {
        response.setHeader("X-Token-Expired", "true");
        logger.debug("Token expired, set X-Token-Expired header");
    }

    // Extract cookie value by name
    private String extractCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}