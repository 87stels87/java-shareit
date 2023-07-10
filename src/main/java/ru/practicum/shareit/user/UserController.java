package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос на создание юзера {}", userDto);
        return userService.create(userDto);
    }

    @GetMapping()
    public Collection<UserDto> findAll() {
        log.info("Получен запрос на получение всех юзеров");
        return userService.findAll();
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId,
                              @Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос на апдейт юзера с id = {}, новые данные: {}", userId, userDto);
        return userService.updateUser(userDto, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("Получен запрос на просмотр юзера с id = {}", userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Получен запрос на удаление юзера с id = {}", userId);
        userService.deleteUserById(userId);
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handle(final NotFoundException e) {
        return Map.of(
                "Сообщение об ошибке", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(final BadRequestException e) {
        return Map.of(
                "Сообщение об ошибке", e.getMessage()
        );
    }
}