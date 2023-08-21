package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto updateUser(UserDto userDto, long userId);

    Collection<UserDto> findAll();

    UserDto getUserById(long userId);

    void deleteUserById(long userId);
}