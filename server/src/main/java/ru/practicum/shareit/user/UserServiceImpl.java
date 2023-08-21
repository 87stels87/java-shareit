package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EmailNotExistException;
import ru.practicum.shareit.helpers.HelperService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final HelperService helperService;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.returnUser(userDto);
        userRepo.save(user);
        return UserMapper.returnUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        User user = UserMapper.returnUser(userDto);
        user.setId(userId);
        helperService.checkUser(userId);

        User newUser = userRepo.findById(userId).get();

        if (user.getName() != null) {
            newUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            List<User> findEmail = userRepo.findByEmail(user.getEmail());

            if (!findEmail.isEmpty() && findEmail.get(0).getId() != userId) {
                throw new EmailNotExistException("there is already a ru.practicum.shareit.user with an email " + user.getEmail());
            }
            newUser.setEmail(user.getEmail());
        }

        userRepo.save(newUser);

        return UserMapper.returnUserDto(newUser);
    }


    @Override
    public List<UserDto> findAll() {
        return UserMapper.returnUserDtoList(userRepo.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(long userId) {
        helperService.checkUser(userId);
        return UserMapper.returnUserDto(userRepo.findById(userId).get());
    }

    @Transactional
    @Override
    public void deleteUserById(long userId) {
        helperService.checkUser(userId);
        userRepo.deleteById(userId);
    }
}