package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.MethodParametersException;
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
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private ItemService itemService;
    @MockBean
    private UserServiceImpl userService;
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
    private Item itemForDto = new Item();
    private final LessShortItemDto lessShortItemDtoRequest = LessShortItemDto.builder()
            .name("Дрель")
            .description("Простая дрель")
            .available(true)
            .requestId(1L)
            .build();
    private ItemDto itemDtoForResponse;
    private LessShortItemDto lessShortItemDtoForResponse;
    private final ShortBookingDto lastBooking = ShortBookingDto.builder()
            .id(1L)
            .bookerId(1L)
            .build();
    private final ShortBookingDto nextBooking = ShortBookingDto.builder()
            .id(2L)
            .bookerId(1L)
            .build();
    private CommentDto commentDtoForResponse;
    private final CommentDto commentDtoRequest = CommentDto.builder()
            .text("Add comment from user1")
            .build();
    private final Comment commentForDto = new Comment();
    private final Booking booking = new Booking();
    private final List<Item> items = new ArrayList<>();
    private final List<ItemDto> itemsDtoResponse = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final Set<CommentDto> comments = new HashSet<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @BeforeEach
    void setUp() {
        request.setId(1L);

        itemForDto = ItemMapper.toItem(lessShortItemDtoRequest);
        itemForDto.setId(1L);
        itemForDto.setOwner(user);
        itemForDto.setRequest(request);

        lessShortItemDtoForResponse = ItemMapper.toLessShortItemDto(itemForDto);

        commentForDto.setId(1L);
        commentForDto.setText("Add comment from user1");
        commentForDto.setAuthor(user);
        commentForDto.setCreated(LocalDateTime.now());

        commentDtoForResponse = CommentMapper.toCommentDto(commentForDto);
        comments.add(commentDtoForResponse);

        itemDtoForResponse = ItemMapper.toItemDto(itemForDto);
        itemDtoForResponse.setLastBooking(lastBooking);
        itemDtoForResponse.setNextBooking(nextBooking);
        itemDtoForResponse.setComments(comments);

        itemsDtoResponse.add(itemDtoForResponse);

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
        when(itemService.create(any(Item.class)))
                .thenReturn(itemForDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(lessShortItemDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(lessShortItemDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(lessShortItemDtoForResponse.getName())))
                .andExpect(jsonPath("$.description", is(lessShortItemDtoForResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(lessShortItemDtoForResponse.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(lessShortItemDtoForResponse.getRequestId()), Long.class));
    }

    @Test
    void shouldReturnPatchedLessShortItemDto() throws Exception {
        lessShortItemDtoRequest.setName("Дрель+");
        lessShortItemDtoRequest.setDescription("Аккумуляторная дрель");
        lessShortItemDtoRequest.setAvailable(false);
        lessShortItemDtoRequest.setRequestId(request.getId());

        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(itemService.getItemById(anyLong()))
                .thenReturn(itemForDto);
        when(requestService.getRequestById(anyLong()))
                .thenReturn(request);

        request.setId(2L);

        itemForDto.setName("Дрель+");
        itemForDto.setDescription("Аккумуляторная дрель");
        itemForDto.setAvailable(false);
        itemForDto.setRequest(request);

        when(itemService.update(any(Item.class)))
                .thenReturn(itemForDto);

        lessShortItemDtoForResponse = ItemMapper.toLessShortItemDto(itemForDto);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(lessShortItemDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(lessShortItemDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(lessShortItemDtoForResponse.getName())))
                .andExpect(jsonPath("$.description", is(lessShortItemDtoForResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(lessShortItemDtoForResponse.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(lessShortItemDtoForResponse.getRequestId()), Long.class));
    }

    @Test
    void shouldReturnItemDtoById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(itemService.getItemDtoByOwnerIdAndItemId(anyLong(), anyLong()))
                .thenReturn(itemDtoForResponse);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoForResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoForResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoForResponse.getAvailable())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemDtoForResponse.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(itemDtoForResponse.getLastBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.id", is(itemDtoForResponse.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(itemDtoForResponse.getNextBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].id", is(commentDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$.comments[0].text", is(commentDtoForResponse.getText())))
                .andExpect(jsonPath("$.comments[0].authorName", is(commentDtoForResponse.getAuthorName())))
                .andExpect(jsonPath("$.comments[0].created", is(commentDtoForResponse.getCreated().format(formatter))));
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
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoForResponse.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoForResponse.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoForResponse.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking.id", is(itemDtoForResponse.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.bookerId", is(itemDtoForResponse.getLastBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.id", is(itemDtoForResponse.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.bookerId", is(itemDtoForResponse.getNextBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].comments", hasSize(1)))
                .andExpect(jsonPath("$[0].comments[0].id", is(commentDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].comments[0].text", is(commentDtoForResponse.getText())))
                .andExpect(jsonPath("$[0].comments[0].authorName", is(commentDtoForResponse.getAuthorName())))
                .andExpect(jsonPath("$[0].comments[0].created", is(commentDtoForResponse.getCreated().format(formatter))));
    }

    @Test
    void shouldReturnMethodParametersException() throws Exception {
        Mockito.when(itemService.getAllItemsDtoByOwnerId(1L, -1, 10))
                .thenThrow(new MethodParametersException("Параметры запроса заданы не верно."));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodParametersException))
                .andExpect(result -> assertEquals("Параметры запроса заданы не верно.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void shouldReturnAllLessShortItemDtoBySearch() throws Exception {
        when(itemService.findItemsBySearch(anyString(), anyInt(), anyInt()))
                .thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "Дрель")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(lessShortItemDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(lessShortItemDtoForResponse.getName())))
                .andExpect(jsonPath("$[0].description", is(lessShortItemDtoForResponse.getDescription())))
                .andExpect(jsonPath("$[0].available", is(lessShortItemDtoForResponse.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(lessShortItemDtoForResponse.getRequestId()), Long.class));
    }

    @Test
    void shouldReturnCommentDto() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(itemService.getItemById(anyLong()))
                .thenReturn(itemForDto);
        when(bookingService.getAllBookingsByBookerId(anyLong()))
                .thenReturn(bookings);
        when(itemService.createComment(any(Comment.class)))
                .thenReturn(commentForDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoForResponse.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoForResponse.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDtoForResponse.getCreated().format(formatter))));
    }
}
