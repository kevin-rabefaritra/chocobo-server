package studio.startapps.chocobo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import studio.startapps.chocobo.security.SecurityConfig;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(SecurityConfig.class)
@AutoConfigureMockMvc
public class SecurityAccessTests {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAccessTests.class);

    @Value("${local.management.port}")
    private int managementPort;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void batchHelloIsATeapot() throws Exception {
        mockMvc.perform(get("/api/batch/hello"))
            .andExpect(status().isIAmATeapot());
    }

    @Test
    void actuatorInfoIsUnauthorized() throws Exception {
        mockMvc.perform(
            options("/api/actuator/info")
                .header("Access-Control-Request-Method", "POST")
                .header("Origin", "http://unauthorized.com")
        )
        .andExpect(status().isForbidden());
    }

    @Test
    void actuatorInfoIsOk() throws Exception {
        mockMvc.perform(
            options("/api/actuator/info")
                .header("Access-Control-Request-Method", "GET")
                .header("Origin", "http://localhost:9090")
        )
        .andExpect(status().isOk());
    }
}
