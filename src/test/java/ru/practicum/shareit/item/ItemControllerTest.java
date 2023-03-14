package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.LessShortItemDto;
import ru.practicum.shareit.item.dto.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @MockBean
    @Qualifier(value = "ItemServiceImpl")
    private ItemService itemService;
    @MockBean
    private UserService userService;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private RequestService requestService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    private final User user = new User(
            1L,
            "user@user.com",
            "user");
    private final Request request = new Request();
    private final LessShortItemDto lessShortItemDtoRequest = LessShortItemDto.builder()
            .name("Дрель")
            .description("Простая дрель")
            .available(true)
            .requestId(1L)
            .build();
    private Item itemForDto;
    private ItemDto itemDtoResponse;
    private LessShortItemDto lessShortItemDtoResponse;
    private final ShortBookingDto lastBooking = ShortBookingDto.builder()
            .id(1L)
            .bookerId(1L)
            .build();
    private final ShortBookingDto nextBooking = ShortBookingDto.builder()
            .id(2L)
            .bookerId(1L)
            .build();
    private final Comment comment = new Comment();
    private CommentDto commentDto;
    private final Booking booking = new Booking();
    private final List<Item> items = new ArrayList<>();
    private final List<ItemDto> itemsDtoResponse = new ArrayList<>();
    private final List<LessShortItemDto> lessShortItemsDtoResponse = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final Set<CommentDto> comments = new HashSet<>();

    @BeforeEach
    void setUp() {
        request.setId(1L);

        itemForDto = ItemMapper.toItem(lessShortItemDtoRequest);
        itemForDto.setId(1L);
        itemForDto.setOwner(user);
        itemForDto.setRequest(request);

        comment.setId(1L);
        comment.setText("Add comment from user1");
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        commentDto = CommentMapper.toCommentDto(comment);
        comments.add(commentDto);

        itemDtoResponse = ItemMapper.toItemDto(itemForDto);
        itemDtoResponse.setLastBooking(lastBooking);
        itemDtoResponse.setNextBooking(nextBooking);
        itemDtoResponse.setComments(comments);

        itemsDtoResponse.add(itemDtoResponse);

        items.add(itemForDto);

        booking.setItem(itemForDto);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStartDate(LocalDateTime.now().minusDays(2L));
        booking.setEndDate(LocalDateTime.now().minusDays(1L));

        bookings.add(booking);
    }

    @Test
    void shouldReturnCreatedLessShortItemDto() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(requestService.getRequestById(anyLong()))
                .thenReturn(request);
        when(itemService.create(any()))
                .thenReturn(itemForDto);

        lessShortItemDtoResponse = ItemMapper.toLessShortItemDto(itemForDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(lessShortItemDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(lessShortItemDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(lessShortItemDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(lessShortItemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(lessShortItemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(lessShortItemDtoResponse.getRequestId()), Long.class));
    }

    @Test
    void shouldReturnPatchedLessShortItemDto() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(itemService.getItemById(anyLong()))
                .thenReturn(itemForDto);
        when(requestService.getRequestById(anyLong()))
                .thenReturn(request);

        itemForDto.setName("Дрель+");
        itemForDto.setDescription("Аккумуляторная дрель");
        itemForDto.setAvailable(false);
        request.setId(2L);
        itemForDto.setRequest(request);

        when(itemService.update(any()))
                .thenReturn(itemForDto);

        lessShortItemDtoResponse = ItemMapper.toLessShortItemDto(itemForDto);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(lessShortItemDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(lessShortItemDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(lessShortItemDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(lessShortItemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(lessShortItemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(lessShortItemDtoResponse.getRequestId()), Long.class));
    }

    @Test
    void shouldReturnItemDtoById() throws Exception {

        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(itemService.getItemDtoByOwnerIdAndItemId(anyLong(), anyLong()))
                .thenReturn(itemDtoResponse);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemDtoResponse.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(itemDtoResponse.getLastBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.id", is(itemDtoResponse.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(itemDtoResponse.getNextBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.comments[0].text", is(commentDto.getText())))
                .andExpect(jsonPath("$.comments[0].authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.comments[0].created", is(commentDto.getCreated().toString())));
    }

    @Test
    void shouldReturnAllItemsDto() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(itemService.getAllItemsDtoByOwnerId(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemsDtoResponse);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .content(mapper.writeValueAsString(itemsDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoResponse.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking.id", is(itemDtoResponse.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.bookerId", is(itemDtoResponse.getLastBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.id", is(itemDtoResponse.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.bookerId", is(itemDtoResponse.getNextBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].comments", hasSize(1)))
                .andExpect(jsonPath("$[0].comments[0].id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].comments[0].text", is(commentDto.getText())))
                .andExpect(jsonPath("$[0].comments[0].authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$[0].comments[0].created", is(commentDto.getCreated().toString())));
    }

    @Test
    void shouldReturnAllLessShortItemDtoBySearch() throws Exception {
        when(itemService.findItemsBySearch(anyString(), anyInt(), anyInt()))
                .thenReturn(items);

        lessShortItemDtoResponse = ItemMapper.toLessShortItemDto(itemForDto);
        lessShortItemsDtoResponse.add(lessShortItemDtoResponse);

        mockMvc.perform(get("/items/search")
                        .param("text", "Дрель")
                        .param("from", "0")
                        .param("size", "10")
                        .content(mapper.writeValueAsString(lessShortItemsDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(lessShortItemDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(lessShortItemDtoResponse.getName())))
                .andExpect(jsonPath("$[0].description", is(lessShortItemDtoResponse.getDescription())))
                .andExpect(jsonPath("$[0].available", is(lessShortItemDtoResponse.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(lessShortItemDtoResponse.getRequestId()), Long.class));
    }

    @Test
    void shouldReturnCommentDto() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(itemService.getItemById(anyLong()))
                .thenReturn(itemForDto);
        when(bookingService.getAllBookingsByBookerId(anyLong()))
                .thenReturn(bookings);
        when(itemService.createComment(any()))
                .thenReturn(comment);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().toString())));
    }
}
