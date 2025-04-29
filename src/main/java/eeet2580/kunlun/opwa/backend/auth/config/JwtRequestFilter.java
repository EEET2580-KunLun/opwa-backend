package eeet2580.kunlun.opwa.backend.auth.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;
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
        String jwtToken = null;

// Skip token validation for authentication endpoints
        if (requestPath.startsWith("/v1/auth/login") ||
                requestPath.startsWith("/v1/auth/register") ||
                requestPath.startsWith("/v1/auth/refresh-token")) {
            logger.info("Skipping token validation for authentication path: " + requestPath);
            chain.doFilter(request, response);
            return;
        }

        // If not found in header, check cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt_token".equals(cookie.getName())) {
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }

        // If the token is found
        if (jwtToken != null) {
            try {
                Claims claims = jwtTokenUtil.getAllClaimsFromToken(jwtToken);
                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                if (email != null && role != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

                    if (!jwtTokenUtil.isTokenExpired(jwtToken)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role)));

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                logger.error("Could not set user authentication in security context", e);
            }
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
}