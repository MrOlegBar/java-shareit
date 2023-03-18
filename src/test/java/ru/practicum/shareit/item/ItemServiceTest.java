package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
public class ItemServiceTest {
    @Autowired
    private ItemService itemService;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private CommentRepository commentRepository;
    private final long userId = 1L;
    private final long itemId = 1L;
    long wrongItemId = 99L;
    private final int from = 0;
    private final int size = 10;
    private final User user = new User(
            1L,
            "user@user.com",
            "user");
    private final Item item = new Item(
            1L,
            "Дрель",
            "Простая дрель",
            true,
            user,
            null,
            new HashSet<>());
    private final Booking lastBooking = new Booking(
            1L,
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().minusDays(1),
            BookingStatus.APPROVED,
            item,
            user);
    private final Booking nextBooking = new Booking(
            2L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2),
            BookingStatus.APPROVED,
            item,
            user);
    private final CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("Add comment from user1")
            .authorName("user")
            .created(LocalDateTime.now())
            .build();
    private final Comment comment = CommentMapper.toComment(commentDto);
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Дрель")
            .description("Простая дрель")
            .available(true)
            .lastBooking(BookingMapper.toShortBookingDto(lastBooking))
            .nextBooking(BookingMapper.toShortBookingDto(nextBooking))
            .comments(Set.of(commentDto))
            .build();

    @Test
    public void shouldReturnCreatedItem() {
        Mockito.when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        Item actual = itemService.create(item);

        assertEquals(item, actual);
    }

    @Test
    public void shouldReturnUpdatedItem() {
        Mockito.when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        Item actual = itemService.update(item);

        assertEquals(item, actual);
    }

    @Test
    public void shouldReturnItemById() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        Item actual = itemService.getItemById(itemId);

        assertEquals(item, actual);
    }

    @Test
    public void shouldReturnItemNotFoundExceptionForGetItemById() {
        Exception exception = assertThrows(ItemNotFoundException.class, () ->
                itemService.getItemById(wrongItemId));

        String expectedMessage = String.format("Вещь с itemId = %s не найдена.",
                wrongItemId);
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldReturnItemByUserIdAndItemId() {
        Mockito.when(itemRepository.findItemByOwner_IdAndId(anyLong(), anyLong()))
                .thenReturn(Optional.of(item));

        Item actual = itemService.getItemByUserIdAndItemId(userId, itemId);

        assertEquals(item, actual);
    }

    @Test
    public void shouldReturnItemNotFoundExceptionForGetItemByUserIdAndItemId() {
        Exception exception = assertThrows(ItemNotFoundException.class, () ->
                itemService.getItemByUserIdAndItemId(userId, wrongItemId));

        String expectedMessage = String.format(String.format("Вещь с itemId = %s у пользователя с userId = %s не найдена.",
                wrongItemId, userId));
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldReturnItemDtoByOwnerIdAndItemId() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(itemRepository.findItemByOwner_IdAndId(anyLong(), anyLong()))
                .thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findFirstByItem_IdAndStatusAndStartDateIsBeforeOrderByEndDateDesc(anyLong(),
                        any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(lastBooking));
        Mockito.when(bookingRepository.findFirstByItem_IdAndStatusAndStartDateIsAfterOrderByStartDateAsc(anyLong(),
                        any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(nextBooking));

        ItemDto foundItemDto = itemService.getItemDtoByOwnerIdAndItemId(userId, itemId);

        assertEquals(itemDto, foundItemDto);
    }

    @Test
    public void shouldReturnAllItemDtoByOwnerId() {
        Mockito.when(itemRepository.findAllByOwner_Id(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(item));

        List<ItemDto> actual = itemService.getAllItemsDtoByOwnerId(userId, from, size);

        assertEquals(List.of(itemDto), actual);
    }

    @Test
    public void shouldReturnAllItemBySearch() {
        Mockito.when(itemRepository.findAllBySearch(anyString(), any(Pageable.class)))
                .thenReturn(List.of(item));

        List<Item> actual = itemService.findItemsBySearch("Дрель", from, size);

        assertEquals(List.of(item), actual);
    }

    @Test
    public void shouldReturnCreatedComment() {
        Mockito.when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        Comment actual = itemService.createComment(comment);

        assertEquals(comment, actual);
    }
}
