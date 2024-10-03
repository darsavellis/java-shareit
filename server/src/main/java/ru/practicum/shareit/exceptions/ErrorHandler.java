package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundException(NotFoundException exception) {
        log.info("Status 404 - Not Found received {}", exception.getMessage());
        return new ErrorResponse("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse permissionException(PermissionException exception) {
        log.info("Status 403 - Forbidden received {}", exception.getMessage());
        return new ErrorResponse("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse internalServerException(Exception exception) {
        log.warn("Status 500 - Internal Error received {}", exception.getMessage(), exception);
        return new ErrorResponse("error", exception.getMessage());
    }
}
