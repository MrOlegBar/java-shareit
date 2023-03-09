package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constraintGroup.Post;
import ru.practicum.shareit.error.MethodParametersException;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class RequestController {
    private final RequestService requestService;
    private final UserService userService;

    @Autowired
    public RequestController(@Qualifier("RequestServiceImpl") RequestService requestService,
                             @Qualifier("UserServiceImpl") UserService userService) {
        this.requestService = requestService;
        this.userService = userService;
    }

    @PostMapping("/requests")
    public RequestDto postItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @Validated(Post.class)
                               @RequestBody RequestDto requestDto) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        Request requestFromDto = RequestMapper.toRequest(requestDto);
        requestFromDto.setRequester(user);
        Request requestForDto = requestService.create(requestFromDto);
        return RequestMapper.toRequestDto(requestForDto);
    }

    @GetMapping(value = {"/requests", "/requests/{requestId}"})
    public Object getRequestS(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable(required = false) Long requestId) throws UserNotFoundException,
            ItemNotFoundException {
        userService.getUserById(userId);

        if (requestId == null) {
            return requestService.getAllRequestsByUserId(userId)
                    .stream()
                    .map(RequestMapper::toRequestDto)
                    .collect(Collectors.toList());
        } else {
            Request requestToDto = requestService.getRequestById(requestId);
            return RequestMapper.toRequestDto(requestToDto);
        }
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