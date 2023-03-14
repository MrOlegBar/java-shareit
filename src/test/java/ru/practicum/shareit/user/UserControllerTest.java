package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    private User userForDto;
    private UserDto userDtoResponse;
    private final List<User> usersForDto = new ArrayList<>();
    private final List<UserDto> usersDtoResponse = new ArrayList<>();

    @BeforeEach
    void setUp() {
        UserDto userDtoRequest = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .build();

        userForDto = UserMapper.toUser(userDtoRequest);
        userForDto.setId(1L);
    }

    @Test
    void shouldReturnCreatedUserDto() throws Exception {
        when(userService.create(any()))
                .thenReturn(userForDto);

        userDtoResponse = UserMapper.toUserDto(userForDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoResponse.getName())))
                .andExpect(jsonPath("$.email", is(userDtoResponse.getEmail())));

    }

    @Test
    void shouldReturnPatchedUserDto() throws Exception {
        when(userService.getUserById(1L))
                .thenReturn(userForDto);

        userForDto.setName("update");
        userForDto.setEmail("update@user.com");

        when(userService.update(any()))
                .thenReturn(userForDto);

        userDtoResponse = UserMapper.toUserDto(userForDto);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoResponse.getName())))
                .andExpect(jsonPath("$.email", is(userDtoResponse.getEmail())));

    }

    @Test
    void shouldReturnUserDtoById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userForDto);

        userDtoResponse = UserMapper.toUserDto(userForDto);

        mockMvc.perform(get("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoResponse.getName())))
                .andExpect(jsonPath("$.email", is(userDtoResponse.getEmail())));

    }

    @Test
    void shouldReturnAllUsersDto() throws Exception {
        usersForDto.add(userForDto);

        when(userService.getAllUsers())
                .thenReturn(usersForDto);

        userDtoResponse = UserMapper.toUserDto(userForDto);
        usersDtoResponse.add(userDtoResponse);

        mockMvc.perform(get("/users")
                        .content(mapper.writeValueAsString(usersDtoResponse))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDtoResponse.getName())))
                .andExpect(jsonPath("$[0].email", is(userDtoResponse.getEmail())));

    }

    @Test
    void shouldReturnTrueAfterDeleting() throws Exception {
        when(userService.deleteUser(anyLong()))
                .thenReturn(true);

        mockMvc.perform(delete("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(true))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));
    }
}