package studio.startapps.chocobo.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import studio.startapps.chocobo.post.internal.PostNotFoundException;

@RestControllerAdvice
public class RequestExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(RequestExceptionHandler.class);

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler({PostNotFoundException.class})
    void handleNotFound() {
        this.logger.info("RequestExceptionHandler.handleNotFound");
    }
}
