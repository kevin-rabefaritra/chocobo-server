package studio.startapps.chocobo.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import studio.startapps.chocobo.auth.internal.RequestToken;
import studio.startapps.chocobo.utils.JwtUtil;

import javax.crypto.SecretKey;

@Service
public class AuthService {

    private final SecretKey accessSecretKey;
    private final int accessTokenTtl;

    public AuthService(
        @Value("${chocobo.jwt.access.secretkey}") String accessSecretKey,
        @Value("${chocobo.jwt.access.ttl}") int accessTokenTtl
    ) {
        this.accessSecretKey = JwtUtil.generateKey(accessSecretKey);
        this.accessTokenTtl = accessTokenTtl;
    }

    public AuthToken renewAccessToken() {
        return JwtUtil.generateTokenSet(this.accessSecretKey);
    }

    public RequestToken toAccessToken(String jwt) {
        return JwtUtil.toRequestToken(jwt, this.accessSecretKey);
    }

    /**
     * Returns true if the provided access token has expired, else false
     * @param token the access token to process
     * @return true or false
     */
    public boolean hasAccessTokenExpired(RequestToken token) {
        return JwtUtil.hasTokenExpired(token, this.accessTokenTtl);
    }
}
