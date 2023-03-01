package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findAllByUser_Id(Long userId);
    Optional<Item> findItemByUser_IdAndId(Long userId, Long itemId);
}