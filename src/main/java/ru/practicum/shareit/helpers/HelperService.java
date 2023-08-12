package ru.practicum.shareit.helpers;

import org.springframework.data.domain.PageRequest;

public interface HelperService {

    void checkItem(Long itemId);

    void checkUser(Long userId);

    void checkBooking(Long booking);

    void checkRequest(Long requestId);

    PageRequest checkPageSize(Integer from, Integer size);
}