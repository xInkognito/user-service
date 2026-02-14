package ru.cinimex.userimpl.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.cinimex.userapi.dto.CodeConfirmationRequest;
import ru.cinimex.userapi.dto.RegisterRequest;
import ru.cinimex.userimpl.domain.TempCodeEntity;
import ru.cinimex.userimpl.domain.UserEntity;
import ru.cinimex.userimpl.exception.InvalidCodeException;
import ru.cinimex.userimpl.exception.UserAlreadyExistsException;
import ru.cinimex.userimpl.exception.UserNotFoundException;
import ru.cinimex.userimpl.mapper.UserMapper;
import ru.cinimex.userimpl.repository.TempCodeRepository;
import ru.cinimex.userimpl.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TempCodeRepository tempCodeRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    @DisplayName("Успешная регистрация: сохранение юзера и генерация кода")
    void registerUser_Success() {
        RegisterRequest request = new RegisterRequest("ivan", "ivan@test.ru", "pass123");
        UserEntity userEntity = new UserEntity();
        UUID userId = UUID.randomUUID();
        userEntity.setId(userId);

        when(userRepository.existsByUsernameOrEmail(any(), any())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(userRepository.save(any())).thenReturn(userEntity);

        UUID resultId = registrationService.registerUser(request);

        assertEquals(userId, resultId);
        verify(userRepository).save(any(UserEntity.class));

        ArgumentCaptor<TempCodeEntity> codeCaptor = ArgumentCaptor.forClass(TempCodeEntity.class);
        verify(tempCodeRepository).save(codeCaptor.capture());

        String savedCode = codeCaptor.getValue().getCode();
        assertEquals(6, savedCode.length());
        assertTrue(savedCode.matches("\\d+")); // только цифры
    }

    @Test
    @DisplayName("Ошибка регистрации: пользователь уже существует")
    void registerUser_AlreadyExists_ThrowsException() {
        when(userRepository.existsByUsernameOrEmail(any(), any())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () ->
                registrationService.registerUser(new RegisterRequest("user", "e@m.ru", "p")));
    }

    @Test
    @DisplayName("Успешное подтверждение регистрации")
    void confirmRegistration_Success() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setActive(false);

        TempCodeEntity tempCode = TempCodeEntity.builder()
                .user(user)
                .code("123456")
                .build();

        CodeConfirmationRequest request = new CodeConfirmationRequest(userId, "123456");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tempCodeRepository.findByUser(user)).thenReturn(Optional.of(tempCode));

        registrationService.confirmRegistration(request);

        assertTrue(user.isActive());
        verify(userRepository).save(user);
        verify(tempCodeRepository).delete(tempCode);
    }

    @Test
    @DisplayName("Ошибка подтверждения: неверный код")
    void confirmRegistration_InvalidCode_ThrowsException() {
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setActive(false);

        TempCodeEntity tempCode = TempCodeEntity.builder().code("111111").build();
        CodeConfirmationRequest request = new CodeConfirmationRequest(userId, "222222");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tempCodeRepository.findByUser(user)).thenReturn(Optional.of(tempCode));

        assertThrows(InvalidCodeException.class, () -> registrationService.confirmRegistration(request));
    }

    @Test
    @DisplayName("Ошибка подтверждения: пользователь не найден")
    void confirmRegistration_UserNotFound_ThrowsException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                registrationService.confirmRegistration(new CodeConfirmationRequest(userId, "123456")));
    }
}