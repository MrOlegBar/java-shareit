package ru.practicum.shareit.item.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;

import java.util.Collection;

@Service("ItemServiceImpl")
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item create(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Item getItemById(long itemId) throws ItemNotFoundException {
        return itemRepository.findById(itemId).orElseThrow(() -> {
            log.debug("Вещь с itemId  = {} найдена.", itemId);
            throw new ItemNotFoundException(String.format("Вещь с itemId = %s не найдена.",
                    itemId));
        });
    }

    @Override
    public Collection<Item> getAllItemsByUserId(long userId) {
        return itemRepository.findAllByUser_Id(userId);
    }

    @Override
    public Item getItemByUserIdAndItemId(long userId, long itemId) throws ItemNotFoundException {
        return itemRepository.findItemByUser_IdAndId(userId, itemId).orElseThrow(() -> {
            log.debug("Вещь с itemId  = {} у пользователя с userId = {} не найдена.", itemId, userId);
            throw new ItemNotFoundException(String.format("Вещь с itemId = %s у пользователя с userId = %s не найдена.",
                    itemId, userId));
        });
    }

    @Override
    public Item update(Item item) throws ItemNotFoundException {
        return itemRepository.save(item);
    }

    @Override
    public Collection<Item> findItemsBySearch(String text) {
        return itemRepository.findItemsBySearch(text.toLowerCase().substring(1));
    }
}