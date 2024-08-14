package studio.startapps.chocobo.auth;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping(path = "/renew")
    AuthToken refresh() {
        return this.authService.renewAccessToken();
    }
}
