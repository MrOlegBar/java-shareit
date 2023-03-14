package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.exception.BookingBadRequestException;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.MethodParametersException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.CommentMapper;
import ru.practicum.shareit.constraintGroup.Post;
import ru.practicum.shareit.constraintGroup.Put;
import ru.practicum.shareit.item.dto.LessShortItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final RequestService requestService;

    @PostMapping("/items")
    public LessShortItemDto postItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @Validated(Post.class)
                                     @RequestBody LessShortItemDto lessShortItemDto) throws UserNotFoundException {
        User user = userService.getUserById(userId);
        Item itemFromDto = ItemMapper.toItem(lessShortItemDto);

        if (lessShortItemDto.getRequestId() != null) {
            Request request = requestService.getRequestById(lessShortItemDto.getRequestId());
            itemFromDto.setRequest(request);
        }

        itemFromDto.setOwner(user);

        Item itemForDto = itemService.create(itemFromDto);
        return ItemMapper.toLessShortItemDto(itemForDto);
    }

    @GetMapping("/items/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable(required = false) Long itemId)
            throws UserNotFoundException,
            ItemNotFoundException {

        userService.getUserById(userId);

        return itemService.getItemDtoByOwnerIdAndItemId(userId, itemId);
    }

    @GetMapping("/items")
    public Collection<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                        @RequestParam(required = false, defaultValue = "10") Integer size)
            throws UserNotFoundException,
            ItemNotFoundException {

        userService.getUserById(userId);
        if (from < 0 || size <= 0) {
            log.debug("Параметры запроса заданы не верно.");
            throw new MethodParametersException("Параметры запроса заданы не верно.");
        }

        return itemService.getAllItemsDtoByOwnerId(userId, from, size);
    }

    @PatchMapping("/items/{itemId}")
    public LessShortItemDto putItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Validated(Put.class) @RequestBody LessShortItemDto lessShortItemDto,
                                    @PathVariable Long itemId) throws UserNotFoundException, ItemNotFoundException {
        User user = userService.getUserById(userId);
        Item item = itemService.getItemById(itemId);

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
            Request request = requestService.getRequestById(lessShortItemDto.getRequestId());
            item.setRequest(request);
        }

        item.setOwner(user);

        Item itemForDto = itemService.update(item);
        return ItemMapper.toLessShortItemDto(itemForDto);
    }

    @GetMapping("/items/search")
    public List<LessShortItemDto> findItemsBySearch(@RequestParam String text,
                                                    @RequestParam(required = false, defaultValue = "0") Integer from,
                                                    @RequestParam(required = false, defaultValue = "10") Integer size) {
        if (from < 0 || size <= 0) {
            log.debug("Параметры запроса заданы не верно.");
            throw new MethodParametersException("Параметры запроса заданы не верно.");
        }

        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemService.findItemsBySearch(text, from, size).stream()
                .map(ItemMapper::toLessShortItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/items/{itemId}/comment")
    public CommentDto postComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @Validated(Post.class) @RequestBody CommentDto commentDto,
                                  @PathVariable Long itemId) throws UserNotFoundException {

        User user = userService.getUserById(userId);
        Item item = itemService.getItemById(itemId);

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

        Comment commentToDto = itemService.createComment(commentFromDto);
        return CommentMapper.toCommentDto(commentToDto);
    }
}