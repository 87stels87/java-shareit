package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class UserController {

    private final UserServiceImpl userService;


    @PostMapping()
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        log.info("create user by data {}", userDto);
        return userService.create(userDto);
    }

    @GetMapping()
    public List<UserDto> findAll() {
        log.info("find all users:");
        return userService.findAll();
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("update user by id = {}, new data: {}", userId, userDto);
        return userService.updateUser(userDto, userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("get user by id = {}", userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.info("delete user by id", userId);
        userService.deleteUserById(userId);
    }
}