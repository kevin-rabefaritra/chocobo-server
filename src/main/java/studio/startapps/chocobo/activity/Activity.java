package studio.startapps.chocobo.activity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import studio.startapps.chocobo.activity.internal.ActivityActionEnum;

import java.time.LocalDateTime;

@Document(collection = "activity")
@Data @AllArgsConstructor @NoArgsConstructor
public class Activity {

    @Id
    private String id;

    private String remoteAddr;

    private String sessionId;

    private ActivityActionEnum action;

    private String comment;

    @NotBlank
    private String postId;

    @NotNull
    private LocalDateTime performedOn;
}
