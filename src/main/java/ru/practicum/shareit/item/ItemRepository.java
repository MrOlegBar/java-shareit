package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findAllByUser_Id(Long userId);
    Optional<Item> findItemByUser_IdAndId(Long userId, Long itemId);
    @Query("SELECT i FROM Item i WHERE i.available = TRUE AND (LOWER(i.name) LIKE CONCAT('%', :text, '%') OR " +
            "LOWER(i.description) LIKE CONCAT('%', :text, '%'))")
    Collection<Item> findItemsBySearch(@Param("text") String text);
}
