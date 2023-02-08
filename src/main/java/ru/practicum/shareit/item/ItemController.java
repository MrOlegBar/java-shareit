package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final UserService userService;

    @Autowired
    public ItemController(@Qualifier("ItemServiceImpl") ItemService itemService, ItemMapper itemMapper,
                          @Qualifier("UserServiceImpl") UserService userService) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
        this.userService = userService;
    }

    @PostMapping("/items")
    public ItemDto postItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @Valid @RequestBody Item item) throws UserNotFoundException {
        userService.getUserById(userId);

        item.setUserId(userId);
        Item itemForDto = itemService.create(item);
        return itemMapper.toDto(itemForDto);
    }

    @GetMapping(value = { "/items", "/items/{itemId}"})
    public Object getItemS(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable(required = false) Long itemId) throws UserNotFoundException,
            ItemNotFoundException {
        userService.getUserById(userId);

        if (itemId == null) {
            return itemService.getAllItemsByUserId(userId).stream()
                    .map(itemMapper::toDto)
                    .collect(Collectors.toSet());
        } else {
            Item itemForDto = itemService.getItemById(itemId);
            return itemMapper.toDto(itemForDto);
        }
    }

    @PatchMapping("/items/{itemId}")
    public ItemDto putItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId,
                           @Valid @RequestBody ItemDto itemDto) throws UserNotFoundException, ItemNotFoundException {
        userService.getUserById(userId);

        Item itemFromDto = itemMapper.toItem(itemDto);
        itemFromDto.setId(itemId);

        Item itemForDto = itemService.update(userId, itemFromDto);
        return itemMapper.toDto(itemForDto);
    }

    @GetMapping( "/items/search")
    public Collection<ItemDto> findFilmsBySearch(@RequestParam String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemService.findItemsBySearch(text).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }
}