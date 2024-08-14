package studio.startapps.chocobo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import studio.startapps.chocobo.auth.AuthToken;
import studio.startapps.chocobo.auth.internal.RequestToken;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;

/**
 * Class responsible for managing JWT
 */
public interface JwtUtil {

    String TOKEN_ISSUED_ON = "issued_at";

    /**
     * Generates a token set (access token + refresh token) given the username and secrets
     * @param accessSecret the access token secret
     * @return AuthTokenSet instance containing both tokens
     */
    static AuthToken generateTokenSet(SecretKey accessSecret) {
        String accessToken = generateKey(accessSecret);
        return new AuthToken(accessToken);
    }

    static String generateKey(SecretKey key) {
        LocalDateTime now = DateUtils.now();
        return Jwts.builder()
            .claim(TOKEN_ISSUED_ON, DateUtils.formatDateTimeISO(now))
            .signWith(key)
            .compact();
    }

    /**
     * Decodes a token
     * @param jwt The token to be decoded
     * @return Payload of the token
     */
    private static Claims decode(String jwt, SecretKey secretKey) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(jwt)
            .getPayload();
    }

    /**
     * Generates a secret key
     * @param key The secret to be used to generate the key
     * @return a SecretKey instance
     */
    static SecretKey generateKey(String key) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }

    /**
     * Returns true or false whether the token has expired.
     * @param token token (access or refresh) to be processed
     * @param ttl token TTL (in minutes)
     * @return A boolean stating if the token has expired
     */
    static boolean hasTokenExpired(RequestToken token, long ttl) {
        LocalDateTime now = DateUtils.now();
        LocalDateTime expiresOnDateTime = token.issuedOn().plusMinutes(ttl);
        return expiresOnDateTime.isBefore(now);
    }

    /**
     * Extracts the subject / issued on from JWT
     * @param jwt token to be processed
     * @param secretKey key to use
     * @return Subject extracted from the token
     */
    static RequestToken toRequestToken(String jwt, SecretKey secretKey) {
        Claims claims = decode(jwt, secretKey);
        return new RequestToken(
            DateUtils.parseDateTimeISO(claims.get(TOKEN_ISSUED_ON).toString())
        );
    }
}
