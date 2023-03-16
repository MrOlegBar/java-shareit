package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
public class ItemServiceImplTest {
    @Autowired
    private ItemServiceImpl itemService;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private CommentRepository commentRepository;
    private Comment testComment;
    private Comment comment;
    private Set<Comment> comments;
    private ItemDto testItemDto;
    private List<ItemDto> testItemsDto;
    private Item item;
    private Item testItem;
    private List<Item> testItems;
    private Booking lastBooking;
    private Booking nextBooking;

    @BeforeEach
    public void setUp() {
        comments = new HashSet<>();
        Request request = new Request();
        User user = new User(1L, "user@user.com", "user");

        item = new Item("Дрель", "Простая дрель", true, user, request, comments);

        testItem = new Item(1L, "Дрель", "Простая дрель", true, user, request, comments);

        comment = new Comment("Add comment from user1", user, testItem);
        testComment = new Comment(1L, "Add comment from user1", user, testItem);

        lastBooking = new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                BookingStatus.APPROVED, testItem, user);
        nextBooking = new Booking(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED, testItem, user);

        testItemDto = ItemMapper.toItemDto(testItem);
        testItemDto.setLastBooking(BookingMapper.toShortBookingDto(lastBooking));
        testItemDto.setNextBooking(BookingMapper.toShortBookingDto(nextBooking));

        testItemsDto = new ArrayList<>();
        testItemsDto.add(testItemDto);

        testItems = new ArrayList<>();
        testItems.add(testItem);
    }

    @Test
    public void shouldReturnCreatedItem() {
        Mockito.when(itemRepository.save(any(Item.class)))
                .thenReturn(testItem);

        Item foundItem = itemService.create(item);

        assertNotNull(foundItem);
        assertEquals(testItem, foundItem);
    }

    @Test
    public void shouldReturnUpdatedItem() {
        testItem.setName("Дрель+");
        testItem.setDescription("Аккумуляторная дрель");
        testItem.setAvailable(false);

        Mockito.when(itemRepository.save(any(Item.class)))
                .thenReturn(testItem);

        Item foundItem = itemService.update(item);

        assertNotNull(foundItem);
        assertEquals(testItem, foundItem);
    }

    @Test
    public void shouldReturnItemById() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testItem));

        Item foundItem = itemService.getItemById(1L);

        assertNotNull(foundItem);
        assertEquals(testItem, foundItem);
    }

    @Test
    public void shouldReturnItemNotFoundException() {
        Mockito.when(itemRepository.findById(99L))
                .thenThrow(new ItemNotFoundException(String.format("Вещь с itemId = %s не найдена.",
                        99L)));

        Exception exception = assertThrows(ItemNotFoundException.class, () -> {
            itemService.getItemById(99L);
        });

        String expectedMessage = "Вещь с itemId = 99 не найдена.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    public void shouldReturnItemByUserIdAndItemId() {
        Mockito.when(itemRepository.findItemByOwner_IdAndId(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(testItem));

        Item foundItem = itemService.getItemByUserIdAndItemId(1L, 1L);

        assertNotNull(foundItem);
        assertEquals(testItem, foundItem);
    }

    @Test
    public void shouldReturnItemDtoByOwnerIdAndItemId() {
        Mockito.when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testItem));
        Mockito.when(itemRepository.findItemByOwner_IdAndId(anyLong(), anyLong()))
                .thenReturn(Optional.ofNullable(testItem));
        Mockito.when(bookingRepository.findFirstByItem_IdAndStatusAndStartDateIsBeforeOrderByEndDateDesc(anyLong(),
                        any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(Optional.ofNullable(lastBooking));
        Mockito.when(bookingRepository.findFirstByItem_IdAndStatusAndStartDateIsAfterOrderByStartDateAsc(anyLong(),
                        any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(Optional.ofNullable(nextBooking));

        ItemDto foundItemDto = itemService.getItemDtoByOwnerIdAndItemId(1L, 1L);

        assertNotNull(foundItemDto);
        assertEquals(testItemDto, foundItemDto);
    }

    @Test
    public void shouldReturnAllItemBySearch() {
        Mockito.when(itemRepository.findItemsBySearch(anyString(), any(PageRequest.class)))
                .thenReturn(testItems);

        List<Item> foundItems = itemService.findItemsBySearch("Дрель", 0, 10);

        assertNotNull(foundItems);
        assertEquals(testItems, foundItems);
    }

    @Test
    public void shouldReturnAllItemDtoByOwnerId() {
        Mockito.when(itemRepository.findAllByOwner_Id(anyLong(), any(PageRequest.class)))
                .thenReturn(testItems);

        List<ItemDto> foundItemsDto = itemService.getAllItemsDtoByOwnerId(1L, 0, 10);

        assertNotNull(foundItemsDto);
        assertEquals(testItemsDto, foundItemsDto);
    }

    @Test
    public void shouldReturnCreatedComment() {
        Mockito.when(commentRepository.save(any(Comment.class)))
                .thenReturn(testComment);

        Comment foundComment = itemService.createComment(comment);

        assertNotNull(foundComment);
        assertEquals(testComment, foundComment);
    }
}
