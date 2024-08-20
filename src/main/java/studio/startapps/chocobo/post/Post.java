package studio.startapps.chocobo.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import studio.startapps.chocobo.post.internal.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "posts")
@Data
@AllArgsConstructor @NoArgsConstructor
public class Post {

    @Id
    private String id;

    @TextIndexed
    @NotBlank
    private String titleId;

    @NotBlank
    private String title;

    private String description;

    private String keywords;

    private String tags;

    @NotNull
    private LocalDate publishedOn;

    @NotBlank
    private String author;

    @NotBlank
    private String media;

    private int duration;

    private int viewCount;

    @NotBlank
    private String thumbnail;

    @NotNull
    private MediaType type;

    private boolean available;
}
