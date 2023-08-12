package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingOutputDto> createBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid BookingDto bookingDto
    ) {
        log.info("Create booking {}", userId);
        return ResponseEntity.ok(bookingService.addBooking(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingOutputDto> approve(
            @PathVariable Long bookingId,
            @RequestParam boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Approve booking id = {}, approved: {}", bookingId, approved);
        return ResponseEntity.ok(bookingService.approveBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingOutputDto> bookingOutputDto(@PathVariable Long bookingId,
                                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get booking by id {}", bookingId);
        return ResponseEntity.ok(bookingService.getBookingById(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<List<BookingOutputDto>> getAllBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @RequestParam(defaultValue = "0", required = false) Integer from,
            @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        log.info("Get all bookings, userId = {}, state = {}", userId, state);
        return ResponseEntity.ok(bookingService.getAllBookingsByBookerId(userId, state, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingOutputDto>> getAllBookingsForOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @RequestParam(defaultValue = "0", required = false) Integer from,
            @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        log.info("Get all bookings by  owner, userId = {}, state = {}", userId, state);
        return ResponseEntity.ok(bookingService.getAllBookingsForAllItemsByOwnerId(userId, state, from, size));
    }
}