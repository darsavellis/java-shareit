package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validationException(ValidationException exception) {
        log.info("Status 400 - Bad Request received {}", exception.getMessage());
        return new ErrorResponse("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse internalServerException(Exception exception) {
        log.warn("Status 500 - Internal Error received {}", exception.getMessage(), exception);
        return new ErrorResponse("error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.warn("Status 400 - Bad Request received {}", exception.getMessage());
        return new ErrorResponse("error", exception.getMessage());
    }
}
