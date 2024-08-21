package studio.startapps.chocobo.activity;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import studio.startapps.chocobo.activity.internal.ActivityRequest;
import studio.startapps.chocobo.activity.internal.UserReportsCountExceedsException;

@RestController
@RequestMapping(path = "api/activity")
@AllArgsConstructor
public class ActivityController {

    private static final String REAL_IP_HEADER = "X-Real-IP";

    private final ActivityService activityService;

    @PostMapping(path = "/watch")
    void watch(@RequestBody ActivityRequest activityRequest, HttpServletRequest request) {
        String remoteAddr = this.getRemoteAddr(request);
        this.activityService.saveWatch(activityRequest, remoteAddr);
    }

    @PostMapping(path = "/report")
    @ResponseStatus(code = HttpStatus.CREATED)
    void report(@RequestBody ActivityRequest activityRequest, HttpServletRequest request) throws UserReportsCountExceedsException {
        String remoteAddr = this.getRemoteAddr(request);
        this.activityService.saveReport(activityRequest, remoteAddr);
    }

    @PostMapping(path = "/share")
    void share(@RequestBody ActivityRequest activityRequest, HttpServletRequest request) {
        String remoteAddr = this.getRemoteAddr(request);
        this.activityService.saveShare(activityRequest, remoteAddr);
    }

    private String getRemoteAddr(HttpServletRequest request) {
        String xRealIP = request.getHeader(ActivityController.REAL_IP_HEADER);
        if (xRealIP != null && !xRealIP.isBlank()) {
            return xRealIP;
        }
        return request.getRemoteAddr();
    }

    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null) {
            return authorization.split(" ")[1];
        }
        return null;
    }
}
