package studio.startapps.chocobo.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import studio.startapps.chocobo.activity.internal.UserReportsCountExceedsException;
import studio.startapps.chocobo.post.internal.PostNotFoundException;
import studio.startapps.chocobo.post.internal.UnauthorizedPostException;

@RestControllerAdvice
public class RequestExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(RequestExceptionHandler.class);

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler({PostNotFoundException.class})
    void handleNotFound() {
        this.logger.info("RequestExceptionHandler.handleNotFound");
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({UnauthorizedPostException.class})
    void handleUnauthorized() {
        this.logger.info("RequestExceptionHandler.handleUnauthorized");
    }

    /**
     * UserReportsCountExceedsException is added to be used as a honey pot
     */
    @ResponseStatus(value = HttpStatus.CREATED)
    @ExceptionHandler({UserReportsCountExceedsException.class})
    void handleCreated() {
        this.logger.info("RequestExceptionHandler.handleCreated");
    }
}
