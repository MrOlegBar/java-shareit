package ru.practicum.shareit.gateway.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.constraintGroup.Post;
import ru.practicum.shareit.gateway.error.MethodParametersException;
import ru.practicum.shareit.gateway.user.UserNotFoundException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RequestController {
    private final RequestClient requestClient;
    private static final String DEFAULT_FROM_VALUE = "0";
    private static final String DEFAULT_SIZE_VALUE = "10";
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping("/requests")
    public ResponseEntity<Object> postRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @Validated(Post.class)
                                              @RequestBody RequestDtoForRequest requestDtoForRequest) throws UserNotFoundException {

        return requestClient.postRequest(requestDtoForRequest, userId);
    }

    @GetMapping({"/requests", "/requests/{requestId}"})
    public ResponseEntity<Object> getRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable(required = false) Long requestId)
            throws UserNotFoundException, RequestNotFoundException {

        if (requestId == null) {
            return requestClient.getRequests(userId);
        } else {
            return requestClient.getRequest(requestId, userId);
        }
    }

    @GetMapping("/requests/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(USER_ID_HEADER) Long userId,
                                                     @RequestParam(required = false, defaultValue = DEFAULT_FROM_VALUE)
                                                     Integer from,
                                                     @RequestParam(required = false, defaultValue = DEFAULT_SIZE_VALUE)
                                                     Integer size)
            throws UserNotFoundException {

        if (from < 0 || size <= 0) {
            log.debug("Параметры запроса from & size заданы не верно.");
            throw new MethodParametersException("Параметры запроса from & size заданы не верно.");
        }

        return requestClient.getAllItemRequests(from, size, userId);
    }
}
