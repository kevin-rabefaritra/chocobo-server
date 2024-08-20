package studio.startapps.chocobo.post;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import studio.startapps.chocobo.post.internal.UnauthorizedPostException;

@RestController
@RequestMapping(path = "/api/batch")
@AllArgsConstructor
public class PostBatchController {

    private final Logger logger = LoggerFactory.getLogger(PostBatchController.class);

    private final static String KEY_HEADER = "C-Key";

    private final PostService postService;

    @PostMapping(path = "/posts")
    @ResponseStatus(code = HttpStatus.CREATED)
    void directSave(
        @RequestPart("post") Post post,
        @RequestParam("thumbnailFile") MultipartFile thumbnailFile,
        @RequestParam("mediaFile") MultipartFile mediaFile,
        @RequestHeader(PostBatchController.KEY_HEADER) String key
    ) throws UnauthorizedPostException {
        this.logger.info("PostBatchController.directSave");
        this.postService.save(post, thumbnailFile, mediaFile, key);
    }

    @GetMapping(path = "/hello")
    @ResponseStatus(code = HttpStatus.I_AM_A_TEAPOT)
    void hello() {

    }
}
