package ru.practicum.shareit.item.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDao;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.item.ItemService;

import java.util.Collection;

@Service("ItemServiceImpl")
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;

    @Autowired
    public ItemServiceImpl(@Qualifier("ItemDaoImpl") ItemDao itemDao) {
        this.itemDao = itemDao;
    }

    @Override
    public Item create(Item item) {
        return itemDao.save(item);
    }

    @Override
    public Collection<Item> getAllItemsByUserId(long userId) {
        return itemDao.getAllItemsByUserId(userId);
    }

    @Override
    public Item getItemById(long itemId) throws ItemNotFoundException {
        return itemDao.findByItemId(itemId);
    }

    @Override
    public Item update(Long userId, Item item) throws ItemNotFoundException {
        return itemDao.update(userId, item);
    }

    @Override
    public Collection<Item> findItemsBySearch(String text) {
        return itemDao.findItemsBySearch(text);
    }
}