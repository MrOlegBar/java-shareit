package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.exception.BookingBadRequestException;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.mapper.CommentMapper;
import ru.practicum.shareit.constraintGroup.Post;
import ru.practicum.shareit.constraintGroup.Put;
import ru.practicum.shareit.item.dto.LessShortItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Autowired
    public ItemController(@Qualifier("ItemServiceImpl") ItemService itemService,
                          @Qualifier("UserServiceImpl") UserService userService,
                          @Qualifier("BookingServiceImpl") BookingService bookingService) {
        this.itemService = itemService;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @PostMapping("/items")
    public LessShortItemDto postItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @Validated(Post.class)
                            @RequestBody LessShortItemDto lessShortItemDto) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        Item itemFromDto = ItemMapper.toItem(lessShortItemDto);
        itemFromDto.setOwner(user);
        Item itemForDto = itemService.create(itemFromDto);
        return ItemMapper.toItemDtoForPostOrPut(itemForDto);
    }

    @GetMapping(value = { "/items", "/items/{itemId}"})
    public Object getItemS(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable(required = false) Long itemId) throws UserNotFoundException,
            ItemNotFoundException {
        userService.getUserById(userId);

        if (itemId == null) {
            return itemService.getAllItemsDtoByUserId(userId);
        } else {
            return itemService.getItemDtoByItemId(userId, itemId);
        }
    }

    @PatchMapping("/items/{itemId}")
    public LessShortItemDto putItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Validated(Put.class)
                           @RequestBody LessShortItemDto lessShortItemDto,
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
        item.setOwner(user);

        Item itemForDto = itemService.update(item);
        return ItemMapper.toItemDtoForPostOrPut(itemForDto);
    }

    @GetMapping("/items/search")
    public Collection<LessShortItemDto> findItemsBySearch(@RequestParam String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemService.findItemsBySearch(text).stream()
                .map(ItemMapper::toItemDtoForPostOrPut)
                .collect(Collectors.toList());
    }

    @PostMapping("/items/{itemId}/comment")
    public CommentDto postComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @Validated(Post.class) @RequestBody CommentDto commentDto,
                                  @PathVariable Long itemId) throws UserNotFoundException {

        User user = userService.getUserById(userId);
        Item item = itemService.getItemById(itemId);

        if (bookingService.getAllBookingsByBookerId(userId, BookingState.ALL).stream()
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