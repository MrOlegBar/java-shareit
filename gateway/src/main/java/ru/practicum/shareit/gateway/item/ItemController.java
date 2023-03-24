package ru.practicum.shareit.gateway.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.constraintGroup.Patch;
import ru.practicum.shareit.gateway.constraintGroup.Post;
import ru.practicum.shareit.gateway.item.dto.CommentDto;
import ru.practicum.shareit.gateway.item.dto.LessShortItemDto;
import ru.practicum.shareit.gateway.user.UserClient;
import ru.practicum.shareit.gateway.user.UserNotFoundException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    private static final String DEFAULT_FROM_VALUE = "0";
    private static final String DEFAULT_SIZE_VALUE = "10";
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemClient itemClient;
    private final UserClient userClient;

    @PostMapping("/items")
    public ResponseEntity<Object> postItem(@Validated({Post.class})
                                           @RequestBody LessShortItemDto lessShortItemDto,
                                           @RequestHeader(USER_ID_HEADER) Long userId) {
        return itemClient.postItem(lessShortItemDto, userId);
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<Object> patchItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                            @Validated(Patch.class) @RequestBody LessShortItemDto lessShortItemDto,
                                            @PathVariable Long itemId) {
        return itemClient.patchItem(lessShortItemDto, itemId, userId);
    }

    @GetMapping({"/items", "/items/{itemId}"})
    public ResponseEntity<Object> getItemsS(@RequestHeader(USER_ID_HEADER) Long userId,
                                            @RequestParam(required = false, defaultValue = DEFAULT_FROM_VALUE)
                                            @PositiveOrZero Integer from,
                                            @RequestParam(required = false, defaultValue = DEFAULT_SIZE_VALUE)
                                            @Positive Integer size,
                                            @PathVariable(required = false) Long itemId)
            throws UserNotFoundException, ItemNotFoundException {

        /*if (from < 0 || size <= 0) {
            log.debug("Параметры запроса заданы не верно.");
            throw new MethodParametersException("Параметры запроса заданы не верно.");
        }*/

        if (itemId == null) {
            return itemClient.getItems(userId, from, size);
        } else {
            return itemClient.getItem(itemId, userId);
        }
    }

    @GetMapping("/items/search")
    public ResponseEntity<Object> getItemsBySearch(@RequestParam String text,
                                                   @RequestHeader(USER_ID_HEADER) Long userId,
                                                   @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                   @PositiveOrZero Integer from,
                                                   @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                   @Positive Integer size) {
        /*if (from < 0 || size <= 0) {
            log.debug("Параметры запроса заданы не верно.");
            throw new MethodParametersException("Параметры запроса заданы не верно.");
        }*/

        return itemClient.getItemsBySearch(text, userId, from, size);
    }

    @PostMapping("/items/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                                @Validated(Post.class) @RequestBody CommentDto commentDto,
                                                @PathVariable Long itemId) {
        return itemClient.postComment(commentDto, itemId, userId);
    }
}