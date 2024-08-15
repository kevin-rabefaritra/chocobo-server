package studio.startapps.chocobo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import studio.startapps.chocobo.auth.AuthService;
import studio.startapps.chocobo.post.PostController;
import studio.startapps.chocobo.post.PostService;

@WebMvcTest(controllers = PostController.class)
@ActiveProfiles({"test"})
class ChocoboApplicationTests {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	PostService postService;

	@MockBean
	AuthService authService;

	@Test
	void contextLoads() {

	}
}
