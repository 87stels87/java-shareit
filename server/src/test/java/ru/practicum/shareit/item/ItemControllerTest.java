package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto1;

    private ItemDto itemDto2;

    private CommentDto commentDto;

    private ItemRequest itemRequest;

    private User user;

    @BeforeEach
    void beforeEach() {

        user = User.builder()
                .id(1L)
                .name("andrey")
                .email("andrey@yandex.ru")
                .build();

        itemRequest = ItemRequest.builder()
                .id(2L)
                .description("ivan")
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .created(LocalDateTime.now())
                .authorName("Artur")
                .build();

        itemDto1 = ItemDto.builder()
                .id(1L)
                .name("hammer")
                .description("steel hammer")
                .available(true)
                .comments(List.of(commentDto))
                .requestId(itemRequest.getId())
                .build();

        itemDto2 = ItemDto.builder()
                .id(1L)
                .name("saw")
                .description("good saw")
                .available(true)
                .comments(Collections.emptyList())
                .requestId(itemRequest.getId())
                .build();
    }

    @Test
    void addItem() throws Exception {
        when(itemService.addNewItem(anyLong(), any(ItemDto.class))).thenReturn(itemDto1);

        mvc.perform(post("/items")
                .content(mapper.writeValueAsString(itemDto1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto1.getRequestId()), Long.class));

        verify(itemService, times(1)).addNewItem(1L, itemDto1);
    }

    @Test
    void changeItem() throws Exception {
        when(itemService.changeItem(any(ItemDto.class), anyLong(), anyLong())).thenReturn(itemDto1);

        mvc.perform(patch("/items/{itemId}", 1L)
                .content(mapper.writeValueAsString(itemDto1))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto1.getRequestId()), Long.class));

        verify(itemService, times(1)).changeItem(itemDto1, 1L, 1L);
    }

    @Test
    void getItemInfoById() throws Exception {
        when(itemService.getItemInfoById(anyLong(), anyLong())).thenReturn(itemDto1);

        mvc.perform(get("/items/{itemId}", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto1.getRequestId()), Long.class));

        verify(itemService, times(1)).getItemInfoById(1L, 1L);
    }

    @Test
    void getAllItemsUser() throws Exception {

        when(itemService.getItemsByUserId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemDto1, itemDto2));

        mvc.perform(get("/items")
                .param("from", "0")
                .param("size", "10")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto1, itemDto2))));

        verify(itemService, times(1)).getItemsByUserId(1L, 0, 10);
    }

    @Test
    void getSearchItem() throws Exception {
        when(itemService.getItemsByKeyword(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto1, itemDto2));

        mvc.perform(get("/items/search")
                .param("text", "text")
                .param("from", "0")
                .param("size", "10")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto1, itemDto2))));

        verify(itemService, times(1)).getItemsByKeyword("text", 0, 10);
    }

    @Test
    void addComment() throws Exception {
        when(itemService.postComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                .content(mapper.writeValueAsString(commentDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDto)));

        verify(itemService, times(1)).postComment(1L, 1L, commentDto);
    }
}