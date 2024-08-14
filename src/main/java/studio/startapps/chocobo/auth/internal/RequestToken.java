package studio.startapps.chocobo.auth.internal;

import java.time.LocalDateTime;

public record RequestToken(
    LocalDateTime issuedOn
) {
}
