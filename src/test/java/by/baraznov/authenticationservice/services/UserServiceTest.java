package by.baraznov.authenticationservice.services;

import by.baraznov.authenticationservice.models.User;
import by.baraznov.authenticationservice.repositories.UserRepository;
import by.baraznov.authenticationservice.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void test_getUserByLogin() {
        String login = "testuser";
        User user = new User();
        user.setLogin(login);
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByLogin(login);

        assertThat(result).isPresent();
        assertThat(result.get().getLogin()).isEqualTo(login);
        verify(userRepository).findByLogin(login);
    }

    @Test
    void test_getUserById() {
        int userId = 1;
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(userId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void test_createUser() {
        User user = new User();
        user.setLogin("newuser");

        userService.create(user);

        verify(userRepository).save(user);
    }

    @Test
    void test_existsByLogin() {
        String login = "existinguser";
        when(userRepository.existsByLogin(login)).thenReturn(true);

        Boolean exists = userService.existsByLogin(login);

        assertThat(exists).isTrue();
        verify(userRepository).existsByLogin(login);
    }
}
