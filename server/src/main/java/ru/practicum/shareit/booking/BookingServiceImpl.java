package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.helpers.HelperService;
import ru.practicum.shareit.item.ItemRepo;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepo;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final ItemRepo itemRepo;
    private final HelperService helperService;

    @Transactional
    @Override
    public BookingOutputDto addBooking(BookingDto bookingDto, long userId) {

        Item item = itemRepo.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("ru.practicum.shareit.item not found"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("ru.practicum.shareit.user not found"));

        Booking booking = BookingMapper.returnBooking(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);

        if (item.getOwner().equals(user)) {
            throw new NotFoundException(User.class, "Owner " + userId + " can't book his ru.practicum.shareit.item");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Item " + item.getId() + " is booked");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Start cannot be later than end");
        }
        if (booking.getStart().isEqual(booking.getEnd())) {
            throw new ValidationException("Start cannot be equal than end");
        }

        bookingRepo.save(booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingOutputDto approveBooking(long userId, long bookingId, Boolean approved) {

        helperService.checkBooking(bookingId);
        Booking booking = bookingRepo.findById(bookingId).get();
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException(User.class, "Only owner " + userId + " items can change ru.practicum.shareit.booking status");
        }

        if (approved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new ValidationException("Incorrect status update ru.practicum.shareit.request");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        bookingRepo.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingOutputDto getBookingById(long userId, long bookingId) {

        helperService.checkBooking(bookingId);
        Booking booking = bookingRepo.findById(bookingId).get();

        helperService.checkUser(userId);

        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundException(User.class, "To get information about the reservation, the car of the reservation or the owner {} " + userId + "of the ru.practicum.shareit.item can");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingOutputDto> getAllBookingsByBookerId(long userId, String state, Integer from, Integer size) {

        helperService.checkUser(userId);
        PageRequest pageRequest = helperService.checkPageSize(from, size);

        Page<Booking> bookings = null;
        BookingState bookingState = BookingState.getEnumValue(state);

        switch (bookingState) {
            case ALL:
                bookings = bookingRepo.findAllByBookerIdOrderByStartDesc(userId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepo.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookings = bookingRepo.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepo.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookings = bookingRepo.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepo.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageRequest);
                break;

        }
        return BookingMapper.returnBookingDtoList(bookings);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingOutputDto> getAllBookingsForAllItemsByOwnerId(long userId, String state, Integer from, Integer size) {

        helperService.checkUser(userId);
        PageRequest pageRequest = helperService.checkPageSize(from, size);
        if (itemRepo.findByOwnerIdOrderById(userId).isEmpty()) {
            throw new ValidationException("User does not have for ru.practicum.shareit.booking");
        }


        Page<Booking> bookings = null;
        BookingState bookingState = BookingState.getEnumValue(state);

        switch (bookingState) {
            case ALL:
                bookings = bookingRepo.findAllByItemOwnerIdOrderByStartDesc(userId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepo.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookings = bookingRepo.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepo.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookings = bookingRepo.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepo.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageRequest);
                break;
        }
        return BookingMapper.returnBookingDtoList(bookings);
    }
}