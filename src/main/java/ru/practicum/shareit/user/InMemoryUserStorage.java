package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    public final HashMap<Long, User> users = new HashMap<>();
    private static long id = 0L;

    @Override
    public User create(User user) {
        if (users.values().stream().noneMatch(u -> Objects.equals(user.getEmail(), u.getEmail()))) {
            Long userId = ++id;
            log.info("New user added, userId = {}, user = {}", userId, user);
            user.setId(userId);
            users.put(userId, user);
            return user;
        } else {
            throw new ValidationException("С таким email юзер существует");
        }
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User update(User user, Long userId) {
        if (!users.containsKey(userId)) throw new ValidationException("С таким id юзера нет");
        if (users.values().stream()
                .filter(u -> !Objects.equals(u.getId(), userId))
                .anyMatch(u -> Objects.equals(user.getEmail(), u.getEmail())))
            throw new ValidationException("С таким емейл юзер существует");
        User userUpdate = users.get(userId);
        user.setId(userId);
        if (user.getName() == null) user.setName(userUpdate.getName());
        if (user.getEmail() == null) user.setEmail(userUpdate.getEmail());
        users.put(userId, user);
        log.info("Юзер с id = {} обновлен", userId);
        return user;
    }

    @Override
    public void deleteById(Long userId) {
        if (!users.containsKey(userId)) throw new BadRequestException("такого id нет , удалить не получиться");
        users.remove(userId);
        log.info("юзер с id = {} удален", userId);
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }
}