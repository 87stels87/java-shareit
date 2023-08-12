package ru.practicum.shareit.helpers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.BookingRepo;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepo;
import ru.practicum.shareit.request.ItemRequestRepo;
import ru.practicum.shareit.user.UserRepo;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class HelpersServiceTest {
    @Autowired
    private HelperService helperService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private ItemRepo itemRepo;

    @MockBean
    private BookingRepo bookingRepo;

    @MockBean
    private ItemRequestRepo itemRequestRepo;

    @Test
    void testUser() {
        when(userRepo.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> helperService.checkUser(2L));
    }

    @Test
    void testItem() {
        when(itemRepo.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> helperService.checkItem(2L));
    }

    @Test
    void testBooking() {
        when(bookingRepo.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> helperService.checkBooking(2L));
    }

    @Test
    void testRequest() {
        when(itemRequestRepo.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> helperService.checkRequest(2L));
    }

    @Test
    void testPageSize() {
        assertThrows(ValidationException.class, () -> helperService.checkPageSize(0, 0));
        assertThrows(ValidationException.class, () -> helperService.checkPageSize(10, -10));
        assertThrows(ValidationException.class, () -> helperService.checkPageSize(10, 0));
        assertThrows(ValidationException.class, () -> helperService.checkPageSize(-10, 5));
    }
}