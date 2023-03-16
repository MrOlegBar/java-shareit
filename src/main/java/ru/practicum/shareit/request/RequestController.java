package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constraintGroup.Post;
import ru.practicum.shareit.error.MethodParametersException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.dto.ShortRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RequestController {
    private final RequestService requestService;
    private final UserService userService;

    @PostMapping("/requests")
    public RequestDto postRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @Validated(Post.class)
                                  @RequestBody ShortRequestDto shortRequestDto) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        Request requestFromDto = RequestMapper.toRequest(shortRequestDto);
        requestFromDto.setRequester(user);
        Request requestForDto = requestService.create(requestFromDto);
        return RequestMapper.toRequestDto(requestForDto);
    }

    @GetMapping("/requests/{requestId}")
    public RequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long requestId) throws UserNotFoundException,
            RequestNotFoundException {
        userService.getUserById(userId);

        Request requestForDto = requestService.getRequestById(requestId);
        return RequestMapper.toRequestDto(requestForDto);
    }

    @GetMapping("/requests")
    public Collection<RequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId)
            throws UserNotFoundException {
        userService.getUserById(userId);

        return requestService.getAllRequestsByUserId(userId)
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = {"/requests/all"})
    public List<RequestDto> getAllItemRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(required = false, defaultValue = "0") Integer from,
                                               @RequestParam(required = false, defaultValue = "1") Integer size)
            throws UserNotFoundException {

        userService.getUserById(userId);
        if (from < 0 || size <= 0) {
            log.debug("Параметры запроса заданы не верно.");
            throw new MethodParametersException("Параметры запроса заданы не верно.");
        }

        return requestService.getAllItemRequests(userId, from, size).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }
}