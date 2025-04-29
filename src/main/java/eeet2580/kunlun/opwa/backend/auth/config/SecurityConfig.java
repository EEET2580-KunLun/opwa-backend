package eeet2580.kunlun.opwa.backend.auth.config;

import eeet2580.kunlun.opwa.backend.auth.handler.CustomAuthenticationEntryPoint;
import eeet2580.kunlun.opwa.backend.auth.handler.OAuth2AuthenticationSuccessHandler;
import eeet2580.kunlun.opwa.backend.auth.service.AuthService;
import eeet2580.kunlun.opwa.backend.auth.service.impl.OAuth2UserServiceImpl;
import eeet2580.kunlun.opwa.backend.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthService authService;
    private final JwtTokenUtil jwtTokenUtil;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Define how HTTP requests should be secured
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) // enable the CSRF protection and store the CSRF token in a cookie

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v1/auth/**", "/oauth2/**", "/login/**", "/csrf").permitAll()
                        .anyRequest().authenticated())

                .oauth2Login(oauth2 -> oauth2 // Sign in with Google
                        .successHandler(oAuth2AuthenticationSuccessHandler())
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService())))

                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(customAuthenticationEntryPoint()))  // Add this line
                .addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(authService);

        return http.build();
    }


    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    /**
     * Extract the authenticated user as a StaffEntity
     * Generate a JWT token
     * Construct a redirect URL to the frontend
     */
    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(jwtTokenUtil);
    }

    @Bean
    public OAuth2UserServiceImpl oAuth2UserService() {
        return new OAuth2UserServiceImpl(staffRepository, passwordEncoder);
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter(authService, jwtTokenUtil);
    }

    @Autowired
    private Environment environment;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(Objects.requireNonNull(environment.getProperty("app.frontend.base-url"))));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-XSRF-TOKEN"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}