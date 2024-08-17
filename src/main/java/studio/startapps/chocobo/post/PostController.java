package studio.startapps.chocobo.post;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import studio.startapps.chocobo.post.internal.PostNotFoundException;
import studio.startapps.chocobo.post.internal.UnauthorizedPostException;

import java.util.List;

@RestController
@RequestMapping(path = "/api/posts")
@AllArgsConstructor
public class PostController {

    private final static int PAGE_DEFAULT_SIZE = 9;

    private final static String KEY_HEADER = "Key";

    private final PostService postService;

    @GetMapping
    Page<Post> findAll(@PageableDefault(sort = "publishedOn", direction = Sort.Direction.DESC, size = PAGE_DEFAULT_SIZE) Pageable pageable) {
        return this.postService.findAll(pageable);
    }

    @GetMapping(path = "/search/{keyword}")
    Page<Post> search(
        @PageableDefault(sort = "publishedOn", direction = Sort.Direction.DESC, size = PAGE_DEFAULT_SIZE) Pageable pageable,
        @PathVariable String keyword
    ) {
        return this.postService.findByKeyword(keyword, pageable);
    }

    /**
     * Saves a Post
     * The main difference is that when the method argument is not a String, @RequestParam relies on type conversion
     * via a registered Converter or PropertyEditor while @RequestPart relies on HttpMessageConverters taking into
     * consideration the 'Content-Type' header of the request part. @RequestParam is likely to be used with
     * name-value form fields while @RequestPart is likely to be used with parts containing more complex content
     * (e.g. JSON, XML).
     * https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestPart.html
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void save(
        @RequestPart("post") Post post,
        @RequestParam("thumbnailFile") MultipartFile thumbnailFile,
        @RequestParam("mediaFile") MultipartFile mediaFile,
        @RequestHeader(PostController.KEY_HEADER) String key
    ) throws UnauthorizedPostException {
        this.postService.save(post, thumbnailFile, mediaFile, key);
    }

    @GetMapping(path = "/popular")
    Page<Post> findPopular(@PageableDefault(sort = "viewCount", direction = Sort.Direction.DESC, size = PAGE_DEFAULT_SIZE) Pageable pageable) {
        return this.postService.findAll(pageable);
    }

    @GetMapping("/{titleId}")
    Post findByTitleId(
            @PathVariable String titleId,
            @RequestParam(name = "availableOnly", defaultValue = "true", required = false) String availableOnly
    ) throws PostNotFoundException {
        return this.postService.findByTitleId(titleId, Boolean.parseBoolean(availableOnly));
    }

    @PutMapping("/{postId}")
    void update(
        @PathVariable String postId,
        @RequestPart("post") Post post,
        @RequestParam(name = "thumbnailFile", required = false) MultipartFile thumbnailFile,
        @RequestParam(name = "mediaFile", required = false) MultipartFile mediaFile,
        @RequestHeader(PostController.KEY_HEADER) String key
    ) throws PostNotFoundException, UnauthorizedPostException {
        this.postService.update(postId, post, thumbnailFile, mediaFile, key);
    }

    @GetMapping(path = "/{titleId}/similar")
    List<Post> findSimilar(
            @PathVariable String titleId,
            @RequestParam(name = "size", defaultValue = "6") int size
    ) throws PostNotFoundException {
        int pageSize = Math.min(size, PAGE_DEFAULT_SIZE);
        List<Post> result = this.postService.findSimilarByTitleId(titleId, pageSize);

        // Fallback to Posts
        if (result.isEmpty()) {
            result = this.postService.findAll(Pageable.ofSize(PAGE_DEFAULT_SIZE)).toList();
        }
        return result;
    }

    @GetMapping(path = "/sync")
    void updateViewCount() {
        this.postService.updateViewCount();
    }
}
