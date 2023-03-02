package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constraintGroup.Post;
import ru.practicum.shareit.constraintGroup.Put;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public ItemController(@Qualifier("ItemServiceImpl") ItemService itemService,
                          @Qualifier("UserServiceImpl") UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @PostMapping("/items")
    public ItemDto postItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @Validated(Post.class)
                            @RequestBody ItemDto itemDto) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        Item itemFromDto = ItemMapper.toItem(itemDto);
        itemFromDto.setUser(user);
        Item itemForDto = itemService.create(itemFromDto);
        return ItemMapper.toDto(itemForDto);
    }

    @GetMapping(value = { "/items", "/items/{itemId}"})
    public Object getItemS(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable(required = false) Long itemId) throws UserNotFoundException,
            ItemNotFoundException {
        userService.getUserById(userId);

        if (itemId == null) {
            return itemService.getAllItemsByUserId(userId).stream()
                    .map(ItemMapper::toDto)
                    .collect(Collectors.toSet());
        } else {
            Item itemForDto = itemService.getItemById(itemId);
            return ItemMapper.toDto(itemForDto);
        }
    }

    @PatchMapping("/items/{itemId}")
    public ItemDto putItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @Validated(Put.class)
                           @RequestBody ItemDto itemDto,
                           @PathVariable Long itemId) throws UserNotFoundException, ItemNotFoundException {
        User user = userService.getUserById(userId);
        Item item = itemService.getItemByUserIdAndItemId(userId, itemId);

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        item.setUser(user);

        Item itemForDto = itemService.update(item);
        return ItemMapper.toDto(itemForDto);
    }

    @GetMapping("/items/search")
    public Collection<ItemDto> findItemsBySearch(@RequestParam String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemService.findItemsBySearch(text).stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }
}