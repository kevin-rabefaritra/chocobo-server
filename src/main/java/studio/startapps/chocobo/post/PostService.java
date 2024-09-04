package studio.startapps.chocobo.post;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import studio.startapps.chocobo.activity.ActivityService;
import studio.startapps.chocobo.file.FileStorageService;
import studio.startapps.chocobo.post.internal.PostNotFoundException;
import studio.startapps.chocobo.post.internal.UnauthorizedPostException;
import studio.startapps.chocobo.utils.DateUtils;
import studio.startapps.chocobo.utils.StreamUtils;
import studio.startapps.chocobo.utils.StringUtils;

import java.util.*;

@Service
public class PostService {

    private static final int KEYWORD_MIN_LENGTH = 4;
    private static final String TAG_KEYWORD_SEPARATOR = ";";
    private static final int DUPLICATE_TITLE_SUFFIX_MAX_DIGITS = 5;

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

        String titleId = this.generateTitleId(post.getTitle(), true);
        String filename = StringUtils.generateRandom();
        String savedThumbnail = this.fileStorageService.saveThumbnail(thumbnailFile, filename);
        String savedMedia = this.fileStorageService.saveMedia(mediaFile, filename);

        // Fill post other info
        post.setThumbnail(savedThumbnail);
        post.setMedia(savedMedia);
        post.setViewCount(0);
        post.setTitleId(titleId);

        if (post.getPublishedOn() == null) {
            post.setPublishedOn(DateUtils.today());
        }

        // Make sure the id is auto-generated
        post.setId(null);

        return this.postRepository.save(post);
    }

    Post update(String postId, Post post, MultipartFile thumbnailFile, MultipartFile mediaFile, String masterKey) throws PostNotFoundException, UnauthorizedPostException {
        if (!masterKey.equals(this.masterKey)) {
            throw new UnauthorizedPostException();
        }

        // Apply the changes on the old Post
        Post updatedPost = this.findById(postId);

        // If the title has been changed, we generate a new title ID
        String titleId = updatedPost.getTitleId();
        if (!updatedPost.getTitle().equals(post.getTitleId())) {
            titleId = this.generateTitleId(post.getTitle(), true);
        }

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

        // Remove duplicates
        result = result.stream().filter(StreamUtils.distinctByKey(Post::getId)).toList();

        // Limit results to the page size
        if (result.size() > size) {
            result = result.subList(0, size - 1); // -1 because [toIndex] is inclusive
        }
        return result;
    }

    /**
     * Updates view count for all videos based on the Activity
     */
    void updateViewCount() {
        List<Post> posts = this.postRepository.findAll();
        posts.forEach((item) -> {
            int viewCount = this.activityService.countViewsByPostId(item.getId());
            if (viewCount > 0) {
                item.setViewCount(viewCount);
                this.postRepository.save(item);
            }
        });
    }

    /**
     * Generates a slugified title ID based on a title
     * @param title
     * @param unique If set to true, numbers are appended to the result until the title is unique
     * @return Slugified title ID
     */
    String generateTitleId(String title, boolean unique) {
        String result = StringUtils.slugify(title);

        if (unique && this.postRepository.existsByTitleId(result)) {
            result = StringUtils.appendNumberedSuffix(result, PostService.DUPLICATE_TITLE_SUFFIX_MAX_DIGITS);
            return this.generateTitleId(result, unique);
        }

        return result;
    }

    /**
     * Returns a list of the top {count} tags used by the top {pageSize} videos (ordered by viewCount)
     * @param count
     * @return
     */
    Set<String> getTopTags(int count, int pageSize) {
        Set<String> result = new HashSet<>(count);
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by("viewCount").descending());
        Page<Post> posts = this.findAll(pageable);

        posts.forEach((post) -> {
            String[] postTags = post.getTags().split(TAG_KEYWORD_SEPARATOR);
            List<String> tags = Arrays.stream(postTags).filter((item) -> item != null && !item.isBlank()).toList();
            result.addAll(tags);
        });

        return result;
    }
}
