package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public UserDto create(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getName() == null)
            throw new BadRequestException("Email или имя = null");
        return UserMapper.toUserDto(inMemoryUserStorage.create(UserMapper.toUser(userDto)));
    }

    public Collection<UserDto> findAll() {
        return inMemoryUserStorage.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto updateUser(UserDto userDto, Long userId) {
        return UserMapper.toUserDto(inMemoryUserStorage.update(UserMapper.toUser(userDto), userId));
    }

    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(inMemoryUserStorage.getById(userId)
                .orElseThrow(() -> new ValidationException("Такого id нет")));
    }

    public void deleteUserById(Long userId) {
        inMemoryUserStorage.deleteById(userId);
    }
}
