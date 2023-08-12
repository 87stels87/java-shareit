package ru.practicum.shareit.booking;


import lombok.Getter;
import ru.practicum.shareit.exceptions.NotProcessStatusException;

@Getter
public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static BookingState getEnumValue(String state) {

        try {
            return BookingState.valueOf(state);
        } catch (Exception e) {
            throw new NotProcessStatusException("Unknown bookingstate: " + state);
        }

    }
}