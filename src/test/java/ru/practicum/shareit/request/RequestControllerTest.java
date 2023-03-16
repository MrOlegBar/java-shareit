package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.LessShortItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.dto.ShortRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
public class RequestControllerTest {
    @MockBean
    private UserService userService;

    @MockBean
    private RequestService requestService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    private final ShortRequestDto shortRequestDtoForRequest = ShortRequestDto.builder()
            .description("Хотел бы воспользоваться щёткой для обуви")
            .build();
    private final User user = new User(
            1L,
            "user@user.com",
            "user");
    private LessShortItemDto lessShortItemDto;
    private final Item item = new Item();
    private Request requestForDto;
    private final Request request = new Request();
    private RequestDto requestDtoForResponse;
    private final Set<Item> items = new HashSet<>();
    private final List<Request> requests = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @BeforeEach
    void setUp() {
        request.setId(1L);

        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Простая дрель");
        item.setAvailable(true);
        item.setRequest(request);

        items.add(item);

        requestForDto = RequestMapper.toRequest(shortRequestDtoForRequest);
        requestForDto.setId(1L);
        requestForDto.setRequester(user);
        requestForDto.setItems(items);

        requests.add(requestForDto);

        lessShortItemDto = ItemMapper.toLessShortItemDto(item);

        requestDtoForResponse = RequestMapper.toRequestDto(requestForDto);

    }

    @Test
    void shouldReturnCreatedRequestDto() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(requestService.create(any()))
                .thenReturn(requestForDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(shortRequestDtoForRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoForResponse.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDtoForResponse.getCreated().format(formatter))));
    }

    @Test
    void shouldReturnRequestDtoById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(requestService.getRequestById(anyLong()))
                .thenReturn(requestForDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoForResponse.getDescription())))
                .andExpect(jsonPath("$.created", is(requestDtoForResponse.getCreated().format(formatter))))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(lessShortItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.items[0].name", is(lessShortItemDto.getName())))
                .andExpect(jsonPath("$.items[0].description", is(lessShortItemDto.getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(lessShortItemDto.getAvailable())))
                .andExpect(jsonPath("$.items[0].requestId", is(lessShortItemDto.getRequestId()), Long
                        .class));
    }

    @Test
    void shouldReturnAllRequestDto() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(requestService.getAllRequestsByUserId(anyLong()))
                .thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDtoForResponse.getDescription())))
                .andExpect(jsonPath("$[0].created", is(requestDtoForResponse.getCreated().format(formatter))))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(lessShortItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(lessShortItemDto.getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(lessShortItemDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(lessShortItemDto.getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(lessShortItemDto.getRequestId()), Long
                        .class));
    }

    @Test
    void shouldReturnAllRequestDtoWithPagination() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        when(requestService.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDtoForResponse.getDescription())))
                .andExpect(jsonPath("$[0].created", is(requestDtoForResponse.getCreated().format(formatter))))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(lessShortItemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(lessShortItemDto.getName())))
                .andExpect(jsonPath("$[0].items[0].description", is(lessShortItemDto.getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(lessShortItemDto.getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(lessShortItemDto.getRequestId()), Long
                        .class));
    }
}
