package studio.startapps.chocobo.activity.internal;

public record ActivityRequest(
    String postId,
    String comment,
    String sessionId
) {
}
