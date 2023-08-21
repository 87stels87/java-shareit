package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.helpers.HelperService;
import ru.practicum.shareit.item.ItemRepo;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class RequestServiceTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @MockBean
    private ItemRequestRepo itemRequestRepo;

    @MockBean
    private ItemRepo itemRepo;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private HelperService helperService;


    private User user1;
    private User user2;
    private ItemRequest itemReq1;
    private ItemRequest itemReq2;
    private ItemRequestDto itemRequestDto;
    private Item item;

    @BeforeEach
    void beforeEach() {
        user1 = User.builder()
                .id(1L)
                .name("andrey")
                .email("andrey@yandex.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("ivan")
                .email("ivan@yandex.ru")
                .build();

        itemReq1 = ItemRequest.builder()
                .id(1L)
                .description("req1 text")
                .created(LocalDateTime.now())
                .build();

        itemReq2 = ItemRequest.builder()
                .id(2L)
                .description("req2 text")
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name("hammer")
                .description("steel hammer")
                .available(true)
                .owner(user1)
                .request(itemReq1)
                .build();

        itemRequestDto = ItemRequestDto.builder().description("req1 text").build();
    }

    @Test
    void addRequest() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRequestRepo.save(any(ItemRequest.class))).thenReturn(itemReq1);

        ItemRequestDto itemRequestDtoTest = itemRequestService.addRequest(itemRequestDto, user1.getId());

        assertEquals(itemRequestDtoTest.getId(), itemReq1.getId());
        assertEquals(itemRequestDtoTest.getDescription(), itemReq1.getDescription());

        verify(itemRequestRepo, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getRequests() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepo.findByRequesterIdOrderByCreatedAsc(anyLong())).thenReturn(List.of(itemReq1));
        when(itemRepo.findByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestDto itemRequestDtoTest = itemRequestService.getRequests(user1.getId()).get(0);

        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getName(), item.getName());
        assertEquals(itemRequestDtoTest.getItems().get(0).getDescription(), item.getDescription());
        assertEquals(itemRequestDtoTest.getItems().get(0).getAvailable(), item.getAvailable());

        verify(itemRequestRepo, times(1)).findByRequesterIdOrderByCreatedAsc(anyLong());
    }

    @Test
    void getAllRequests() {
        when(helperService.checkPageSize(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10, 10));
        when(itemRequestRepo.findByIdIsNotOrderByCreatedAsc(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(itemReq1)));
        when(itemRepo.findByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestDto itemRequestDtoTest = itemRequestService.getAllRequests(user1.getId(), 5, 10).get(0);

        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getName(), item.getName());
        assertEquals(itemRequestDtoTest.getItems().get(0).getDescription(), item.getDescription());
        assertEquals(itemRequestDtoTest.getItems().get(0).getAvailable(), item.getAvailable());

        verify(itemRequestRepo, times(1)).findByIdIsNotOrderByCreatedAsc(anyLong(), any(PageRequest.class));
    }

    @Test
    void getRequestById() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepo.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepo.findById(anyLong())).thenReturn(Optional.ofNullable(itemReq1));
        when(itemRepo.findByRequestId(anyLong())).thenReturn(List.of(item));


        ItemRequestDto itemRequestDtoTest = itemRequestService.getRequestById(user1.getId(), itemReq1.getId());

        assertEquals(itemRequestDtoTest.getId(), itemReq1.getId());
        assertEquals(itemRequestDtoTest.getDescription(), itemReq1.getDescription());
        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getRequestId(), user1.getId());

        verify(itemRequestRepo, times(1)).findById(anyLong());
    }

    @Test
    void addItemsToRequest() {
        when(itemRepo.findByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestDto itemRequestDtoTest = itemRequestService.addItemsToRequest(itemReq1);

        assertEquals(itemRequestDtoTest.getItems().get(0).getId(), item.getId());
        assertEquals(itemRequestDtoTest.getItems().get(0).getRequestId(), user1.getId());

        verify(itemRepo, times(1)).findByRequestId(anyLong());
    }
}