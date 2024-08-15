package studio.startapps.chocobo.activity;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import studio.startapps.chocobo.activity.internal.ActivityActionEnum;
import studio.startapps.chocobo.activity.internal.ActivityRequest;
import studio.startapps.chocobo.activity.internal.UserReportsCountExceedsException;
import studio.startapps.chocobo.utils.DateUtils;

import java.time.LocalDateTime;

@Service
public class ActivityService {

    private final int maxReportsPerUser;
    private final int countReportsDays;

    private final ActivityRepository activityRepository;

    public ActivityService(
        @Value("${chocobo.max-reports-per-user}") int maxReportsPerUser,
        @Value("${chocobo.count-reports-days}") int countReportsDays,
        ActivityRepository activityRepository
    ) {
        this.maxReportsPerUser = maxReportsPerUser;
        this.countReportsDays = countReportsDays;
        this.activityRepository = activityRepository;
    }

    private void save(Activity activity) {
        this.activityRepository.save(activity);
    }

    void saveWatch(ActivityRequest activityRequest, String remoteAddr) {
        this.saveActivity(activityRequest, ActivityActionEnum.WATCH, remoteAddr);
    }

    /**
     * Saves a report Activity.
     * We count the number of reports made by the user during the last {countReportsDays} days.
     * If the total number of reports exceeds {maxReportsPerUser}, we throw an UserReportsCountExceedsException
     * @param activityRequest Input activity request
     * @param remoteAddr Remote address of the user
     * @throws UserReportsCountExceedsException
     */
    void saveReport(ActivityRequest activityRequest, String remoteAddr) throws UserReportsCountExceedsException {
        LocalDateTime minDate = DateUtils.now();
        minDate = minDate.minusDays(this.countReportsDays);

        long reportsCount = this.countReportsSinceDate(remoteAddr, minDate);
        if (reportsCount > this.maxReportsPerUser) {
            throw new UserReportsCountExceedsException();
        }

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

    long countReportsSinceDate(String remoteAddr, LocalDateTime minDate) {
        return this.activityRepository.countByRemoteAddrSinceDate(ActivityActionEnum.REPORT, remoteAddr, minDate);
    }
}
