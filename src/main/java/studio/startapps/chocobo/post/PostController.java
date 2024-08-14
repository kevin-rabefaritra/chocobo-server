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

import java.util.List;

@RestController
@RequestMapping(path = "/api/posts")
@AllArgsConstructor
public class PostController {

    private final static int MAX_PAGE_SIZE = 10;

    private final PostService postService;

    @GetMapping
    Page<Post> findAll(@PageableDefault(sort = "publishedOn", direction = Sort.Direction.DESC) Pageable pageable) {
        return this.postService.findAll(pageable);
    }

    @GetMapping(path = "/search/{keyword}")
    Page<Post> search(
        @PageableDefault(sort = "publishedOn", direction = Sort.Direction.DESC) Pageable pageable,
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
        @RequestParam("mediaFile") MultipartFile mediaFile
    ) {
        this.postService.save(post, thumbnailFile, mediaFile);
    }

    @GetMapping(path = "/popular")
    Page<Post> findPopular(@PageableDefault(sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable) {
        return this.postService.findAll(pageable);
    }

    @GetMapping("/{titleId}")
    Post findByTitleId(@PathVariable String titleId) throws PostNotFoundException {
        return this.postService.findByTitleId(titleId);
    }

    @PutMapping("/{postId}")
    void update(
        @PathVariable String postId,
        @RequestPart("post") Post post,
        @RequestParam(name = "thumbnailFile", required = false) MultipartFile thumbnailFile,
        @RequestParam(name = "mediaFile", required = false) MultipartFile mediaFile
    ) throws PostNotFoundException {
        this.postService.update(postId, post, thumbnailFile, mediaFile);
    }

    @GetMapping(path = "/{titleId}/similar")
    List<Post> findSimilar(@PathVariable String titleId) throws PostNotFoundException {
        return this.postService.findSimilarByTitleId(titleId);
    }

    @GetMapping(path = "/sync")
    void updateViewCount() {
        this.postService.updateViewCount();
    }
}
