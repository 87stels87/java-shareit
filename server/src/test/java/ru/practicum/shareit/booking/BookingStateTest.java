package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.NotProcessStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BookingStateTest {
    @Test
    void getEnumValue() {

        String bookingStateStr = "Unknown";
        String finalStateStr = bookingStateStr;
        assertThrows(NotProcessStatusException.class, () -> BookingState.getEnumValue(finalStateStr));

        bookingStateStr = "ALL";
        BookingState stateTest = BookingState.getEnumValue(bookingStateStr);
        assertEquals(stateTest, BookingState.ALL);

        bookingStateStr = "CURRENT";
        stateTest = BookingState.getEnumValue(bookingStateStr);
        assertEquals(stateTest, BookingState.CURRENT);

        bookingStateStr = "PAST";
        stateTest = BookingState.getEnumValue(bookingStateStr);
        assertEquals(stateTest, BookingState.PAST);

        bookingStateStr = "FUTURE";
        stateTest = BookingState.getEnumValue(bookingStateStr);
        assertEquals(stateTest, BookingState.FUTURE);

        bookingStateStr = "REJECTED";
        stateTest = BookingState.getEnumValue(bookingStateStr);
        assertEquals(stateTest, BookingState.REJECTED);

        bookingStateStr = "WAITING";
        stateTest = BookingState.getEnumValue(bookingStateStr);
        assertEquals(stateTest, BookingState.WAITING);
    }
}
