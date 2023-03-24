package ru.practicum.shareit.server.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.booking.exception.BookingBadRequestException;
import ru.practicum.shareit.server.booking.model.BookingStatus;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.mapper.CommentMapper;
import ru.practicum.shareit.server.constraintGroup.Post;
import ru.practicum.shareit.server.item.dto.LessShortItemDto;
import ru.practicum.shareit.server.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.request.Request;
import ru.practicum.shareit.server.request.service.RequestService;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserNotFoundException;
import ru.practicum.shareit.server.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final UserServiceImpl userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final RequestService requestService;
    private static final String DEFAULT_FROM_VALUE = "0";
    private static final String DEFAULT_SIZE_VALUE = "10";
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping("/items")
    public LessShortItemDto postItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                     @Validated(Post.class)
                                     @RequestBody LessShortItemDto lessShortItemDto) throws UserNotFoundException {
        User user = userService.getUserByIdOrElseThrow(userId);
        Item itemFromDto = ItemMapper.toItem(lessShortItemDto);

        if (lessShortItemDto.getRequestId() != null) {
            Request request = requestService.getRequestByIdOrElseThrow(lessShortItemDto.getRequestId());
            itemFromDto.setRequest(request);
        }

        itemFromDto.setOwner(user);

        Item itemForDto = itemService.create(itemFromDto);
        return ItemMapper.toLessShortItemDto(itemForDto);
    }

    @PatchMapping("/items/{itemId}")
    public LessShortItemDto patchItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                      @RequestBody LessShortItemDto lessShortItemDto,
                                      @PathVariable Long itemId) throws UserNotFoundException, ItemNotFoundException {
        User user = userService.getUserByIdOrElseThrow(userId);
        Item item = itemService.getItemByIdOrElseThrow(itemId);

        if (lessShortItemDto.getName() != null) {
            item.setName(lessShortItemDto.getName());
        }
        if (lessShortItemDto.getDescription() != null) {
            item.setDescription(lessShortItemDto.getDescription());
        }
        if (lessShortItemDto.getAvailable() != null) {
            item.setAvailable(lessShortItemDto.getAvailable());
        }
        if (lessShortItemDto.getRequestId() != null) {
            Request request = requestService.getRequestByIdOrElseThrow(lessShortItemDto.getRequestId());
            item.setRequest(request);
        }

        item.setOwner(user);

        Item itemForDto = itemService.update(item);
        return ItemMapper.toLessShortItemDto(itemForDto);
    }

    @GetMapping({"/items", "/items/{itemId}"})
    public Object getItemsS(@RequestHeader(USER_ID_HEADER) Long userId,
                            @RequestParam(required = false, defaultValue = DEFAULT_FROM_VALUE) Integer from,
                            @RequestParam(required = false, defaultValue = DEFAULT_SIZE_VALUE) Integer size,
                            @PathVariable(required = false) Long itemId)
            throws UserNotFoundException, ItemNotFoundException {

        userService.getUserByIdOrElseThrow(userId);

        if (itemId == null) {
            return itemService.getAllItemsDtoByOwnerId(userId, from, size);
        } else {
            return itemService.getItemDtoByOwnerIdAndItemId(userId, itemId);
        }
    }

    @GetMapping("/items/search")
    public List<LessShortItemDto> getItemsBySearch(@RequestParam String text,
                                                   @RequestParam(required = false, defaultValue = DEFAULT_FROM_VALUE)
                                                   Integer from,
                                                   @RequestParam(required = false, defaultValue = DEFAULT_SIZE_VALUE)
                                                   Integer size) {

        if (text.isEmpty()) return new ArrayList<>();

        return itemService.findItemsBySearch(text, from, size).stream()
                .map(ItemMapper::toLessShortItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/items/{itemId}/comment")
    public CommentDto postComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                  @RequestBody CommentDto commentDto,
                                  @PathVariable Long itemId) throws UserNotFoundException {

        User user = userService.getUserByIdOrElseThrow(userId);
        Item item = itemService.getItemByIdOrElseThrow(itemId);

        if (bookingService.getAllBookingsByBookerId(userId).stream()
                .noneMatch(booking -> Objects.equals(booking.getItem().getId(), itemId) && booking.getBooker().getId()
                        .equals(userId) && booking.getStatus().equals(BookingStatus.APPROVED) && booking.getEndDate()
                        .isBefore(LocalDateTime.now()))) {

            log.debug("Вещь с itemId = {} не найдена в истории бронирования пользователя с userId {}.", itemId, userId);
            throw new BookingBadRequestException(String.format("Вещь с itemId = %s не найдена в истории бронирования " +
                    "пользователя с userId %s.", itemId, userId));
        }

        Comment commentFromDto = CommentMapper.toComment(commentDto);
        commentFromDto.setAuthor(user);
        commentFromDto.setItem(item);

        Comment commentForDto = itemService.createComment(commentFromDto);
        return CommentMapper.toCommentDto(commentForDto);
    }
}