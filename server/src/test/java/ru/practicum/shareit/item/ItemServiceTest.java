package ru.practicum.shareit.item;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepo;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepo;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.helpers.HelperService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @MockBean
    private ItemRepo itemRepo;

    @MockBean
    private CommentRepo commentRepo;

    @MockBean
    private BookingRepo bookingRepo;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private ItemRequestRepo itemRequestRepo;

    @MockBean
    private HelperService helperService;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;
    private ItemRequest itemRequest;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("andrey")
                .email("andrey@yandex.ru")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("text ru.practicum.shareit.request description")
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name("hammer")
                .description("steel hammer")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();

        itemDto = ItemMapper.returnItemDto(item);

        comment = Comment.builder()
                .id(1L)
                .author(user)
                .created(LocalDateTime.now())
                .text("text")
                .build();

        commentDto = CommentMapper.returnCommentDto(comment);

        booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();

        booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void addItem() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepo.findById(anyLong())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepo.save(any(Item.class))).thenReturn(item);

        ItemDto itemDtoTest = itemService.addNewItem(user.getId(), itemDto);

        assertEquals(itemDtoTest.getId(), itemDto.getId());
        assertEquals(itemDtoTest.getDescription(), itemDto.getDescription());

        verify(itemRepo, times(1)).save(any(Item.class));
    }

    @Test
    void updateItem() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findByOwnerId(anyLong())).thenReturn(List.of(item));
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepo.save(any(Item.class))).thenReturn(item);

        ItemDto itemDtoTest = itemService.changeItem(itemDto, item.getId(), user.getId());

        assertEquals(itemDtoTest.getId(), itemDto.getId());
        assertEquals(itemDtoTest.getDescription(), itemDto.getDescription());

        verify(itemRepo, times(1)).save(any(Item.class));
    }

    @Test
    void updateItemNotBelongUser() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));

        when(itemRepo.findByOwnerId(anyLong())).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> itemService.changeItem(itemDto, item.getId(), user.getId()));
    }

    @Test
    void getItemById() {
        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(bookingRepo.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(item.getId(), Status.APPROVED, LocalDateTime.now())).thenReturn(Optional.of(booking1));
        when(bookingRepo.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(item.getId(), Status.APPROVED, LocalDateTime.now())).thenReturn(Optional.of(booking2));
        when(commentRepo.findByItemId(anyLong())).thenReturn(List.of(comment));

        ItemDto itemDtoTest = itemService.getItemInfoById(item.getId(), user.getId());

        assertEquals(itemDtoTest.getId(), item.getId());
        assertEquals(itemDtoTest.getDescription(), item.getDescription());
        assertEquals(itemDtoTest.getAvailable(), item.getAvailable());
        assertEquals(itemDtoTest.getRequestId(), item.getRequest().getId());

        verify(itemRepo, times(1)).findById(anyLong());
    }

    @Test
    void getItemsUser() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(helperService.checkPageSize(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10, 10));
        when(itemRepo.findByOwnerId(anyLong(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(item)));
        when(bookingRepo.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(item.getId(), Status.APPROVED, LocalDateTime.now())).thenReturn(Optional.of(booking1));
        when(bookingRepo.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(item.getId(), Status.APPROVED, LocalDateTime.now())).thenReturn(Optional.of(booking2));
        when(commentRepo.findByItemId(anyLong())).thenReturn(List.of(comment));

        ItemDto itemDtoTest = itemService.getItemsByUserId(user.getId(), 5, 10).get(0);

        assertEquals(itemDtoTest.getId(), item.getId());
        assertEquals(itemDtoTest.getDescription(), item.getDescription());
        assertEquals(itemDtoTest.getAvailable(), item.getAvailable());
        assertEquals(itemDtoTest.getRequestId(), item.getRequest().getId());

        verify(itemRepo, times(1)).findByOwnerId(anyLong(), any(PageRequest.class));
    }

    @Test
    void searchItem() {
        when(helperService.checkPageSize(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10, 10));
        when(itemRepo.search(anyString(), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(item)));

        ItemDto itemDtoTest = itemService.getItemsByKeyword("text", 5, 10).get(0);

        assertEquals(itemDtoTest.getId(), item.getId());
        assertEquals(itemDtoTest.getDescription(), item.getDescription());
        assertEquals(itemDtoTest.getAvailable(), item.getAvailable());
        assertEquals(itemDtoTest.getRequestId(), item.getRequest().getId());

        verify(itemRepo, times(1)).search(anyString(), any(PageRequest.class));
    }

    @Test
    void searchItemEmptyText() {
        when(helperService.checkPageSize(anyInt(), anyInt())).thenReturn(PageRequest.of(5 / 10, 10));

        List<ItemDto> itemDtoTest = itemService.getItemsByKeyword("", 5, 10);

        assertTrue(itemDtoTest.isEmpty());

        verify(itemRepo, times(0)).search(anyString(), any(PageRequest.class));
    }

    @Test
    void addComment() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepo.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class))).thenReturn(Optional.of(booking1));
        when(commentRepo.save(any(Comment.class))).thenReturn(comment);

        CommentDto commentDtoTest = itemService.postComment(user.getId(), item.getId(), commentDto);

        assertEquals(1, comment.getId());
        assertEquals(commentDtoTest.getText(), comment.getText());
        assertEquals(commentDtoTest.getAuthorName(), comment.getAuthor().getName());

        verify(commentRepo, times(1)).save(any(Comment.class));
    }

    @Test
    void addCommentUserNotBookingItem() {
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepo.existsById(anyLong())).thenReturn(true);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepo.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class))).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> itemService.postComment(user.getId(), item.getId(), commentDto));
    }
}