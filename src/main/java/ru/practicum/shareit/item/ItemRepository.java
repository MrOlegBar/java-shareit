package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findAllByOwner_Id(Long userId);
    Optional<Item> findItemByOwner_IdAndId(Long userId, Long itemId);
}