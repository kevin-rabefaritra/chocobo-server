package studio.startapps.chocobo;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import studio.startapps.chocobo.security.SecurityConfig;

@Configuration
@Import({SecurityConfig.class})
public class ChocoboConfiguration {
}
