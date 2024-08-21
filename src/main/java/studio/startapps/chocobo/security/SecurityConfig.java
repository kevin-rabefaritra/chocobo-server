package studio.startapps.chocobo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import studio.startapps.chocobo.auth.AuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final AuthenticationFilter authenticationFilter;
    private final UnauthorizedUserHandler unauthorizedUserHandler;

    public SecurityConfig(AuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
        this.unauthorizedUserHandler = new UnauthorizedUserHandler();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorizeRequests -> {
                authorizeRequests.requestMatchers(HttpMethod.GET, "/api/auth/renew").permitAll();
                authorizeRequests.requestMatchers(HttpMethod.GET, "/api/storage/thumbnails/*", "/api/storage/media/*").permitAll();
                authorizeRequests.requestMatchers(HttpMethod.POST, "/api/batch/posts").permitAll();

                // Prometheus (no authentication required)
                authorizeRequests.requestMatchers(HttpMethod.GET, "/api/actuator/prometheus").permitAll();
                authorizeRequests.anyRequest().authenticated();
            })
            .exceptionHandling(e -> e.accessDeniedHandler(this.unauthorizedUserHandler).authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    private static class UnauthorizedUserHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
            logger.info("UnauthorizedUserHandler.handle");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
