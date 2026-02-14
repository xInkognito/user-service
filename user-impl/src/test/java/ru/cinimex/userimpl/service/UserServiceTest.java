package ru.cinimex.userimpl.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import ru.cinimex.userapi.dto.LoginRequest;
import ru.cinimex.userapi.dto.UserInformationResponse;
import ru.cinimex.userimpl.domain.UserEntity;
import ru.cinimex.userimpl.exception.UserNotFoundException;
import ru.cinimex.userimpl.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Успешный логин: проверка вызова менеджера и генерации токена")
    void loginUser_Success() {
        LoginRequest req = new LoginRequest("admin", "password");
        Authentication mockAuth = mock(Authentication.class);
        String expectedToken = "test.jwt.token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(jwtService.generateToken(mockAuth)).thenReturn(expectedToken);

        String resultToken = userService.loginUser(req);

        assertEquals(expectedToken, resultToken);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(mockAuth);
    }

    @Test
    @DisplayName("Получение информации о пользователе: успешный поиск пользователя")
    void getUserInfo_Success() {
        String username = "ivan";
        UserEntity entity = new UserEntity();
        entity.setUsername(username);
        entity.setEmail("ivan@mail.ru");
        entity.setRole("USER");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(entity));

        UserInformationResponse response = userService.getUserInfo(username);

        assertNotNull(response);
        assertEquals(username, response.getUsername());
        assertEquals("ivan@mail.ru", response.getEmail());
        assertEquals("USER", response.getRole());
    }

    @Test
    @DisplayName("Получение информации о пользователе: ошибка, если пользователь не найден")
    void getUserInfo_NotFound_ThrowsException() {
        String username = "non_existent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserInfo(username));
    }
}