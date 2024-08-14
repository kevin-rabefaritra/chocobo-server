package studio.startapps.chocobo.activity;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import studio.startapps.chocobo.activity.internal.ActivityRequest;

@RestController
@RequestMapping(path = "api/activity")
@AllArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping(path = "/watch")
    void watch(@RequestBody ActivityRequest activityRequest, HttpServletRequest request) {
        String remoteAddr = this.getRemoteAddr(request);
        this.activityService.saveWatch(activityRequest, remoteAddr);
    }

    @PostMapping(path = "/report")
    void report(@RequestBody ActivityRequest activityRequest, HttpServletRequest request) {
        String remoteAddr = this.getRemoteAddr(request);
        this.activityService.saveReport(activityRequest, remoteAddr);
    }

    @PostMapping(path = "/share")
    void share(@RequestBody ActivityRequest activityRequest, HttpServletRequest request) {
        String remoteAddr = this.getRemoteAddr(request);
        this.activityService.saveShare(activityRequest, remoteAddr);
    }

    private String getRemoteAddr(HttpServletRequest request) {
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isBlank()) {
            return xRealIP;
        }
        return request.getRemoteAddr();
    }

    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization != null) {
            return authorization.split(" ")[1];
        }
        return null;
    }
}
