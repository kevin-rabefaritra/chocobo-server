package studio.startapps.chocobo.post;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
class PostScheduledTasks {

    private final Logger logger = LoggerFactory.getLogger(PostScheduledTasks.class);

    private final PostService postService;

    @Scheduled(
        fixedDelay = 6,
        timeUnit = TimeUnit.HOURS
    )
    void updateViewCount() {
        this.logger.info("PostScheduledTasks.updateViewCount");
        this.postService.updateViewCount();
    }
}
