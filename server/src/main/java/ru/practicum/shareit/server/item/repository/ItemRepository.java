package ru.practicum.shareit.server.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.server.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT i FROM Item i WHERE i.owner.id = ?1")
    List<Item> findAllByOwner_Id(Long userId, Pageable pageable);

    @Query(value = "SELECT i from Item i WHERE LOWER(i.name) LIKE %?1% " +
            "OR LOWER(i.description) LIKE %?1% AND i.available = TRUE")
    List<Item> findAllBySearch(String text, Pageable pageable);

    Optional<Item> findItemByOwner_IdAndId(Long userId, Long itemId);
}