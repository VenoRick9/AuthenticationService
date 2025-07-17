package by.baraznov.authenticationservice.services.impl;

import by.baraznov.authenticationservice.models.User;
import by.baraznov.authenticationservice.repositories.UserRepository;
import by.baraznov.authenticationservice.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Optional<User> getUserByLogin(String login) {
        return userRepository.findByLogin(login);
    }
}
