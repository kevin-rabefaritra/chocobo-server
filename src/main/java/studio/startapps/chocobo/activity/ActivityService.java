package studio.startapps.chocobo.activity;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import studio.startapps.chocobo.activity.internal.ActivityActionEnum;
import studio.startapps.chocobo.activity.internal.ActivityRequest;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;

    private void save(Activity activity) {
        this.activityRepository.save(activity);
    }

    void saveWatch(ActivityRequest activityRequest, String remoteAddr) {
        this.saveActivity(activityRequest, ActivityActionEnum.WATCH, remoteAddr);
    }

    void saveReport(ActivityRequest activityRequest, String remoteAddr) {
        this.saveActivity(activityRequest, ActivityActionEnum.REPORT, remoteAddr);
    }

    void saveShare(ActivityRequest activityRequest, String remoteAddr) {
        this.saveActivity(activityRequest, ActivityActionEnum.SHARE, remoteAddr);
    }

    private void saveActivity(ActivityRequest activityRequest, ActivityActionEnum action, String remoteAddr) {
        Activity activity = ActivityService.newInstance(
                activityRequest.postId(),
                action,
                activityRequest.comment(),
                remoteAddr,
                activityRequest.sessionId()
        );
        this.save(activity);
    }

    private static Activity newInstance(String postId, ActivityActionEnum action, String comment, String remoteAddr, String sessionId) {
        Activity activity = new Activity();
        activity.setPostId(postId);
        activity.setComment(comment);
        activity.setAction(action);
        activity.setPerformedOn(LocalDateTime.now());
        activity.setRemoteAddr(remoteAddr);
        activity.setSessionId(sessionId);
        return activity;
    }

    public int countViewsByPostId(String postId) {
        return this.activityRepository.countByPostIdAndActionEquals(postId, ActivityActionEnum.WATCH);
    }
}
