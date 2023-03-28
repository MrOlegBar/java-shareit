package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.request.dto.RequestDto;
import ru.practicum.shareit.server.request.dto.RequestMapper;
import ru.practicum.shareit.server.request.dto.RequestDtoForRequest;
import ru.practicum.shareit.server.request.service.RequestService;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserNotFoundException;
import ru.practicum.shareit.server.user.service.UserServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RequestController {
    private final RequestService requestService;
    private final UserServiceImpl userService;
    private static final String DEFAULT_FROM_VALUE = "0";
    private static final String DEFAULT_SIZE_VALUE = "10";
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping("/requests")
    public RequestDto postRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                  @RequestBody RequestDtoForRequest requestDtoForRequest) throws UserNotFoundException {
        User user = userService.getUserByIdOrElseThrow(userId);

        Request requestFromDto = RequestMapper.toRequest(requestDtoForRequest);
        requestFromDto.setRequester(user);
        Request requestForDto = requestService.create(requestFromDto);
        return RequestMapper.toRequestDto(requestForDto);
    }

    @GetMapping({"/requests", "/requests/{requestId}"})
    public Object getRequestS(@RequestHeader(USER_ID_HEADER) Long userId,
                              @PathVariable(required = false) Long requestId) throws UserNotFoundException,
            RequestNotFoundException {
        userService.getUserByIdOrElseThrow(userId);

        if (requestId == null) {
            return requestService.getAllRequestsByUserId(userId)
                    .stream()
                    .map(RequestMapper::toRequestDto)
                    .collect(Collectors.toList());
        } else {
            Request requestForDto = requestService.getRequestByIdOrElseThrow(requestId);
            return RequestMapper.toRequestDto(requestForDto);
        }
    }

    @GetMapping("/requests/all")
    public List<RequestDto> getAllItemRequests(@RequestHeader(USER_ID_HEADER) Long userId,
                                               @RequestParam(required = false, defaultValue = DEFAULT_FROM_VALUE)
                                               Integer from,
                                               @RequestParam(required = false, defaultValue = DEFAULT_SIZE_VALUE)
                                               Integer size)
            throws UserNotFoundException {

        userService.getUserByIdOrElseThrow(userId);

        return requestService.getAllItemRequests(userId, from, size).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }
}