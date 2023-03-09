package ru.practicum.shareit.error;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.BookingBadRequestException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.request.RequestNotFoundException;
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

    @ExceptionHandler({BookingBadRequestException.class, MethodParametersException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ConversionFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleConversionValidation() {
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRequestParamValidation() {
        return new ErrorResponse("Параметр запроса отсутствует.");
    }

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class, BookingNotFoundException.class,
            RequestNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleSQLError(SQLException e) {
        return new ErrorResponse(e.getMessage().split(":")[0]);
    }
}