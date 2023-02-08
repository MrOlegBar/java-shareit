package ru.practicum.shareit.item.daoImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDao;
import ru.practicum.shareit.item.ItemNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Repository("ItemDaoImpl")
@Slf4j
public class ItemDaoImpl implements ItemDao {
    private final Set<Item> items = new HashSet<>();

    @Override
    public Item save(Item item) {
        item.setId(getId());
        item.setAvailable(true);
        items.add(item);

        log.info("Создана вещь: {}.", item);
        return item;
    }

    @Override
    public Collection<Item> getAllItemsByUserId(long userId) {
        return items.stream()
                .filter(savedItem -> savedItem.getUserId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Item findByItemId(long itemId) throws ItemNotFoundException {
        return items.stream()
                .filter(savedItem -> savedItem.getId() == itemId)
                .findFirst().orElseThrow(() -> {
                    log.debug("Вещь с itemId = {} не найдена.", itemId);
                    throw new ItemNotFoundException(String.format("Вещь с itemId = %s не найдена.", itemId));
                });
    }

    @Override
    public Item update(Long userId, Item item) throws ItemNotFoundException {
        return items.stream()
                .filter(savedItem -> savedItem.getUserId().equals(userId) && savedItem.getId().equals(item.getId()))
                .peek(savedItem -> {
                    savedItem.setName(item.getName() != null ? item.getName() : savedItem.getName());
                    savedItem.setDescription(item.getDescription() != null ? item.getDescription() :
                            savedItem.getDescription());
                    savedItem.setAvailable(item.getAvailable() != null ? item.getAvailable() :
                            savedItem.getAvailable());
                })
                .findFirst().orElseThrow(() -> {
                    log.debug("Вещь с itemId = {} у пользователя с userId = {} не найдена.", item.getId(), userId);
                    throw new ItemNotFoundException(String.format("Вещь с itemId = %s у пользователя с userId = %s " +
                                    "не найдена.", item.getId(), userId));
                });
    }

    @Override
    public Collection<Item> findItemsBySearch(String text) {
        String lowercaseText = text.toLowerCase();
        return items.stream()
                .filter(savedItem -> (savedItem.getAvailable())
                        && (savedItem.getName().toLowerCase().contains(lowercaseText)
                        || savedItem.getDescription().toLowerCase().contains(lowercaseText)))
                .collect(Collectors.toList());
    }

    private long getId() {
        return ++Item.ids;
    }
}