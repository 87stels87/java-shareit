package ru.practicum.shareit.exceptions;

public class NotProcessStatusException extends RuntimeException {
    public NotProcessStatusException(String s) {
        super(s);
    }
}