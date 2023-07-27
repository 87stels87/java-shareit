package ru.practicum.shareit.exceptions;

public class EmailNotExistException extends RuntimeException {
    public EmailNotExistException(String s) {
        super(s);
    }
}