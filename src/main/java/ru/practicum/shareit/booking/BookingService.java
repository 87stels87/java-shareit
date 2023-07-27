package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;

import java.util.List;


public interface BookingService {

    BookingOutputDto addBooking(BookingDto bookingDto, long userId);

    BookingOutputDto approveBooking(long userId, long bookingId, Boolean approved);

    BookingOutputDto getBookingById(long userId, long bookingId);

    List<BookingOutputDto> getAllBookingsByBookerId(long userId, String state);

    List<BookingOutputDto> getAllBookingsForAllItemsByOwnerId(long userId, String state);
}