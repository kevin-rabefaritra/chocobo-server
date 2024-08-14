package studio.startapps.chocobo;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import studio.startapps.chocobo.security.SecurityConfig;
import studio.startapps.chocobo.security.WebConfig;

@Configuration
@Import({SecurityConfig.class, WebConfig.class})
public class ChocoboConfiguration {
}
