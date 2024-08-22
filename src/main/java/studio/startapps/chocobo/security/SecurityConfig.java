package studio.startapps.chocobo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import studio.startapps.chocobo.auth.AuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final AuthenticationFilter authenticationFilter;
    private final UnauthorizedUserHandler unauthorizedUserHandler;

    private final String clientOrigin;
    private final String actuatorClientOrigin;

    public SecurityConfig(
            AuthenticationFilter authenticationFilter,
            @Value("${chocobo.cors.client-origin}") String clientOrigin,
            @Value("${chocobo.cors.actuator.client-origin}") String actuatorClientOrigin
    ) {
        this.authenticationFilter = authenticationFilter;
        this.unauthorizedUserHandler = new UnauthorizedUserHandler();
        this.clientOrigin = clientOrigin;
        this.actuatorClientOrigin = actuatorClientOrigin;
    }

    @Bean
    @Order(0)
    public SecurityFilterChain corsFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .securityMatcher("/**")
            .csrf(AbstractHttpConfigurer::disable)
            .cors((cors) -> cors.configurationSource(corsConfigurationSource()));
        return httpSecurity.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorizeRequests -> {
                authorizeRequests.requestMatchers(HttpMethod.GET, "/api/auth/renew").permitAll();
                authorizeRequests.requestMatchers(HttpMethod.GET, "/api/storage/thumbnails/*", "/api/storage/media/*").permitAll();
                authorizeRequests.requestMatchers(HttpMethod.POST, "/api/batch/posts").permitAll();
                authorizeRequests.requestMatchers(HttpMethod.GET,  "/api/batch/hello").permitAll();

                // Prometheus (no authentication required)
                authorizeRequests.requestMatchers(HttpMethod.GET, "/api/actuator/*").permitAll();
                authorizeRequests.anyRequest().authenticated();
            })
            .exceptionHandling(e -> e.accessDeniedHandler(this.unauthorizedUserHandler).authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    CorsConfigurationSource corsConfigurationSource() {
        logger.info("[SecurityConfig.corsConfigurationSource] Registering CORS configuration");

        List<String> protectedEndpoints = List.of("/api/auth/renew", "/api/posts/**", "/api/storage/**", "/api/activity/**");
        List<String> openEndpoints = List.of("/api/batch/**", "/api/batch/hello");
        List<String> actuatorEndpoints = List.of("/api/actuator/*");

        final CorsConfiguration protectedEndpointsConfiguration = new CorsConfiguration();
        protectedEndpointsConfiguration.setAllowedMethods(List.of("*"));
        protectedEndpointsConfiguration.setAllowedOriginPatterns(List.of(this.clientOrigin));

        final CorsConfiguration openEndpointsConfiguration = new CorsConfiguration();
        openEndpointsConfiguration.setAllowedMethods(List.of("POST", "GET"));
        openEndpointsConfiguration.setAllowedOriginPatterns(List.of("*"));

        final CorsConfiguration actuatorEndpointsConfiguration = new CorsConfiguration();
        actuatorEndpointsConfiguration.setAllowedMethods(List.of("GET", "OPTIONS"));
        actuatorEndpointsConfiguration.setAllowedOrigins(List.of(this.actuatorClientOrigin));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        protectedEndpoints.forEach((endpoint) -> {
            source.registerCorsConfiguration(endpoint, protectedEndpointsConfiguration);
        });
        openEndpoints.forEach((endpoint) -> {
            source.registerCorsConfiguration(endpoint, openEndpointsConfiguration);
        });
        actuatorEndpoints.forEach((endpoint) -> {
            source.registerCorsConfiguration(endpoint, actuatorEndpointsConfiguration);
        });
        return source;
    }

    private static class UnauthorizedUserHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
            logger.info("UnauthorizedUserHandler.handle");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
