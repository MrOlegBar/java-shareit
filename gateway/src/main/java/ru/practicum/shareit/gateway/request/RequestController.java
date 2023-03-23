/*
package ru.practicum.shareit.gateway.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RequestController {
    private final RequestService requestService;
    private final UserServiceImpl userService;

    @PostMapping("/requests")
    public RequestDto postRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @Validated(Post.class)
                                  @RequestBody RequestDtoForRequest requestDtoForRequest) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        Request requestFromDto = RequestMapper.toRequest(requestDtoForRequest);
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
}*/