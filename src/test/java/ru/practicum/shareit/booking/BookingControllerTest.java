package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;

    private UserDto user;

    private BookingDto bookingDto;

    private BookingOutputDto bookingOutputDto1;

    private BookingOutputDto bookingOutputDto2;


    @BeforeEach
    void beforeEach() {

        user = UserDto.builder()
                .id(1L)
                .name("Anna")
                .email("anna@yandex.ru")
                .build();

        itemDto = ItemDto.builder()
                .requestId(1L)
                .name("screwdriver")
                .description("works well, does not ask to eat")
                .available(true)
                .build();

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 8, 4, 0, 0))
                .end(LocalDateTime.of(2023, 8, 4, 12, 0))
                .build();

        bookingOutputDto1 = BookingOutputDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 8, 4, 0, 0))
                .end(LocalDateTime.of(2023, 8, 4, 12, 0))
                .item(itemDto)
                .booker(user)
                .status(Status.APPROVED)
                .build();

        bookingOutputDto2 = BookingOutputDto.builder()
                .id(2L)
                .start(LocalDateTime.of(2023, 8, 4, 14, 0))
                .end(LocalDateTime.of(2023, 8, 4, 16, 0))
                .item(itemDto)
                .booker(user)
                .status(Status.APPROVED)
                .build();
    }


    @Test
    void approveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingOutputDto1);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                .param("approved", "true")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutputDto1.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingOutputDto1.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutputDto1.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutputDto1.getItem().getId()), Long.class));

        verify(bookingService, times(1)).approveBooking(1L, 1L, true);
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingOutputDto1);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutputDto1.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingOutputDto1.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutputDto1.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutputDto1.getItem().getId()), Long.class));

        verify(bookingService, times(1)).getBookingById(1L, 1L);
    }

    @Test
    void getAllBookingsByBookerId() throws Exception {
        when(bookingService.getAllBookingsByBookerId(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingOutputDto1, bookingOutputDto2));

        mvc.perform(get("/bookings")
                .param("state", "ALL")
                .param("from", String.valueOf(0))
                .param("size", String.valueOf(10))
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingOutputDto1, bookingOutputDto2))));

        verify(bookingService, times(1)).getAllBookingsByBookerId(1L, "ALL", 0, 10);
    }

    @Test
    void getAllBookingsForAllItemsByOwnerId() throws Exception {
        when(bookingService.getAllBookingsForAllItemsByOwnerId(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingOutputDto1, bookingOutputDto2));

        mvc.perform(get("/bookings/owner")
                .param("state", "ALL")
                .param("from", String.valueOf(0))
                .param("size", String.valueOf(10))
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingOutputDto1, bookingOutputDto2))));

        verify(bookingService, times(1)).getAllBookingsForAllItemsByOwnerId(1L, "ALL", 0, 10);
    }
}