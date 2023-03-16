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
import ru.practicum.shareit.user.service.UserServiceImpl;

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
    private UserServiceImpl userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    private User userForDto;
    private final UserDto userDtoRequest = UserDto.builder()
            .email("user@user.com")
            .name("user")
            .build();
    private UserDto userDtoForResponse;
    private final List<User> usersForDto = new ArrayList<>();

    @BeforeEach
    void setUp() {
        userForDto = UserMapper.toUser(userDtoRequest);
        userForDto.setId(1L);

        usersForDto.add(userForDto);

        userDtoForResponse = UserMapper.toUserDto(userForDto);
    }

    @Test
    void shouldReturnCreatedUserDto() throws Exception {
        when(userService.create(any(User.class)))
                .thenReturn(userForDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoForResponse.getName())))
                .andExpect(jsonPath("$.email", is(userDtoForResponse.getEmail())));

    }

    @Test
    void shouldReturnPatchedUserDto() throws Exception {
        userDtoRequest.setName("update");
        userDtoRequest.setEmail("update@user.com");

        when(userService.getUserById(1L))
                .thenReturn(userForDto);

        userForDto.setName("update");
        userForDto.setEmail("update@user.com");

        when(userService.update(any(User.class)))
                .thenReturn(userForDto);

        userDtoForResponse = UserMapper.toUserDto(userForDto);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoForResponse.getName())))
                .andExpect(jsonPath("$.email", is(userDtoForResponse.getEmail())));

    }

    @Test
    void shouldReturnUserDtoById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userForDto);

        mockMvc.perform(get("/users/{userId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoForResponse.getName())))
                .andExpect(jsonPath("$.email", is(userDtoForResponse.getEmail())));

    }

    @Test
    void shouldReturnAllUsersDto() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(usersForDto);

        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDtoForResponse.getName())))
                .andExpect(jsonPath("$[0].email", is(userDtoForResponse.getEmail())));

    }

    @Test
    void shouldReturnTrueAfterDeleting() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userForDto);
        when(userService.deleteUser(anyLong()))
                .thenReturn(true);

        mockMvc.perform(delete("/users/{userId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));
    }
}