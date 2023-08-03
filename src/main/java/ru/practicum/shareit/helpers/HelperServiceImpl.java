package ru.practicum.shareit.helpers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepo;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepo;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepo;

@Service
@RequiredArgsConstructor
public class HelperServiceImpl implements HelperService {
    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final ItemRepo itemRepo;

    @Override
    public void checkUser(Long userId) {

        if (!userRepo.existsById(userId)) {
            throw new NotFoundException(User.class, "User id " + userId + " not found");
        }
    }

    @Override
    public void checkItem(Long itemId) {

        if (!itemRepo.existsById(itemId)) {
            throw new NotFoundException(Item.class, "Item id " + itemId + " not found");
        }
    }

    @Override
    public void checkBooking(Long bookingId) {

        if (!bookingRepo.existsById(bookingId)) {
            throw new NotFoundException(Booking.class, "Booking id " + bookingId + " not found");
        }
    }
}