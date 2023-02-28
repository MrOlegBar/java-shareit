package ru.practicum.shareit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.user.UserNotFoundException;

import java.sql.SQLException;
import java.util.Objects;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentValidation(MethodArgumentNotValidException e) {
        return new ErrorResponse(String.valueOf(Objects.requireNonNull(e.getFieldError()).getDefaultMessage()));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRequestHeaderValidation(MissingRequestHeaderException e) {
        return new ErrorResponse(String.format("Отсутствует заголовок запроса %s.",
                e.getHeaderName()));
    }

    /*@ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleValidation(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }*/

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(org.postgresql.util.PSQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleSQLError(final SQLException e) {
        return new ErrorResponse(e.getMessage().split("Подробности: ")[1]);
    }
}