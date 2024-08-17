package studio.startapps.chocobo.post;

import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import studio.startapps.chocobo.activity.ActivityService;
import studio.startapps.chocobo.file.FileStorageService;
import studio.startapps.chocobo.post.internal.PostNotFoundException;
import studio.startapps.chocobo.post.internal.UnauthorizedPostException;
import studio.startapps.chocobo.utils.DateUtils;
import studio.startapps.chocobo.utils.StreamUtils;
import studio.startapps.chocobo.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PostService {

    private static final int KEYWORD_MIN_LENGTH = 4;
    private static final String TAG_KEYWORD_SEPARATOR = ";";

    private final String masterKey;

    private final ActivityService activityService;

    private final PostRepository postRepository;
    private final FileStorageService fileStorageService;

    public PostService(
            @Value("${chocobo.master-key}") String masterKey,
            ActivityService activityService,
            PostRepository postRepository,
            FileStorageService fileStorageService
    ) {
        this.masterKey = masterKey;
        this.activityService = activityService;
        this.postRepository = postRepository;
        this.fileStorageService = fileStorageService;
    }

    Page<Post> findAll(Pageable pageable) {
        return this.postRepository.findAllByAvailableTrue(pageable);
    }

    Post findByTitleId(String titleId, boolean availableOnly) throws PostNotFoundException {
        if (availableOnly) {
            return this.postRepository.findByTitleIdAndAvailable(titleId, true).orElseThrow(PostNotFoundException::new);
        }
        else {
            return this.postRepository.findByTitleId(titleId).orElseThrow(PostNotFoundException::new);
        }
    }

    Post findByTitleId(String titleId) throws PostNotFoundException {
        return this.findByTitleId(titleId, true);
    }

    Post findById(String postId) throws PostNotFoundException {
        return this.postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
    }

    Post save(Post post, MultipartFile thumbnailFile, MultipartFile mediaFile, String masterKey) throws UnauthorizedPostException {
        if (!masterKey.equals(this.masterKey)) {
            throw new UnauthorizedPostException();
        }

        String filename = StringUtils.generateRandom();
        String savedThumbnail = this.fileStorageService.saveThumbnail(thumbnailFile, filename);
        String savedMedia = this.fileStorageService.saveMedia(mediaFile, filename);
        String titleId = StringUtils.slugify(post.getTitle());

        // Fill post other info
        post.setThumbnail(savedThumbnail);
        post.setMedia(savedMedia);
        post.setPublishedOn(DateUtils.now());
        post.setViewCount(0);
        post.setTitleId(titleId);

        // Make sure the id is auto-generated
        post.setId(null);

        return this.postRepository.save(post);
    }

    Post update(String postId, Post post, MultipartFile thumbnailFile, MultipartFile mediaFile, String masterKey) throws PostNotFoundException, UnauthorizedPostException {
        if (!masterKey.equals(this.masterKey)) {
            throw new UnauthorizedPostException();
        }

        Post updatedPost = this.findById(postId);
        String titleId = StringUtils.slugify(post.getTitle());

        updatedPost.setTitle(post.getTitle());
        updatedPost.setTitleId(titleId);
        updatedPost.setDescription(post.getDescription());
        updatedPost.setTags(post.getTags());
        updatedPost.setAuthor(post.getAuthor());
        updatedPost.setKeywords(post.getKeywords());
        updatedPost.setAvailable(post.isAvailable());

        // If a new thumbnail or media is provided, we upload them
        if (thumbnailFile != null || mediaFile != null) {
            String filename = StringUtils.generateRandom();

            if (thumbnailFile != null) {
                String savedThumbnail = this.fileStorageService.saveThumbnail(thumbnailFile, filename);
                updatedPost.setThumbnail(savedThumbnail);
            }
            if (mediaFile != null) {
                String savedMedia = this.fileStorageService.saveMedia(thumbnailFile, filename);
                updatedPost.setThumbnail(savedMedia);
            }
        }
        return this.postRepository.save(updatedPost);
    }

    Page<Post> findByKeyword(String keyword, Pageable pageable) {
        keyword = keyword.strip();
        if (keyword.length() < KEYWORD_MIN_LENGTH) {
            return Page.empty();
        }
        return this.postRepository.findByKeyword(keyword, pageable);
    }

    List<Post> findSimilarByTitleId(String titleId, int size) throws PostNotFoundException {
        List<Post> result = new ArrayList<>();
        Post post = this.findByTitleId(titleId);
        String[] tags = post.getTags().split(TAG_KEYWORD_SEPARATOR);

        for (String tag : tags) {
            Page<Post> posts = this.findByKeyword(tag, Pageable.ofSize(size));
            result.addAll(posts.toList());
        }

        // Remove itself
        result.removeIf((item) -> Objects.equals(item.getId(), post.getId()));

        // Remove duplicates and return the result
        return result.stream().filter(StreamUtils.distinctByKey(Post::getId)).toList();
    }

    /**
     * Updates view count for all videos based on the Activity
     */
    void updateViewCount() {
        List<Post> posts = this.postRepository.findAll();
        posts.stream().forEach((item) -> {
            int viewCount = this.activityService.countViewsByPostId(item.getId());
            if (viewCount > 0) {
                item.setViewCount(viewCount);
                this.postRepository.save(item);
            }
        });
    }
}
