package studio.startapps.chocobo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String clientOrigin;
    private final String actuatorClientOrigin;

    public WebConfig(
            @Value("${chocobo.cors.client-origin}") String clientOrigin,
            @Value("${chocobo.cors.actuator.client-origin}") String actuatorClientOrigin) {
        this.clientOrigin = clientOrigin;
        this.actuatorClientOrigin = actuatorClientOrigin;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Default protected endpoints
        List<String> protectedEndpoints = List.of("/api/auth/renew", "/api/posts/**", "/api/storage/**", "/api/activity/**");
        protectedEndpoints.forEach((endpoint) -> {
            registry.addMapping(endpoint)
                    .allowedOrigins(this.clientOrigin)
                    .allowedMethods("*")
                    .allowedHeaders("*")
                    .allowCredentials(true);
        });

        // Actuator endpoints
        registry.addMapping("api/actuator/**")
                .allowedOrigins(this.actuatorClientOrigin)
                .allowedMethods("GET");

        // Batch operations
        List<String> openEndpoints = List.of("/api/batch/**");
        openEndpoints.forEach((endpoint) -> {
            registry.addMapping(endpoint)
                    .allowedOrigins("*")
                    .allowedMethods("POST", "GET");
        });
    }
}
