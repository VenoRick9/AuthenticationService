package by.baraznov.authenticationservice.services;

import by.baraznov.authenticationservice.models.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserByLogin(String login);
}
