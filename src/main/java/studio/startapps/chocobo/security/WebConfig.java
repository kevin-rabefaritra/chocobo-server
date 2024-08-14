package studio.startapps.chocobo.security;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String clientOrigin;

    public WebConfig(@Value("${chocobo.cors.client-origin}") String clientOrigin) {
        this.clientOrigin = clientOrigin;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> protectedEndpoints = List.of("/api/auth/renew", "/api/posts/**", "/api/storage/**", "/api/activity/**");
        protectedEndpoints.forEach((endpoint) -> {
            registry.addMapping(endpoint)
                    .allowedOrigins(this.clientOrigin)
                    .allowedMethods("*")
                    .allowedHeaders("*")
                    .allowCredentials(true);
        });
    }
}
