package ru.cinimex.userimpl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final TempCodeRepository tempCodeRepository;
    private final UserMapper userMapper;

    @Transactional
    public UUID registerUser(RegisterRequest request) {
        if (userRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь с таким именем или email уже существует.");
        }

        UserEntity userEntity = userMapper.toEntity(request);
        userEntity = userRepository.save(userEntity);

        // Генерация 6-значного кода
        String code = String.valueOf((int) ((Math.random() * 900000) + 100000));

        TempCodeEntity tempCode = TempCodeEntity.builder()
                .user(userEntity)
                .code(code)
                .build();

        tempCodeRepository.save(tempCode);

        // TODO: Отправка в Kafka

        return userEntity.getId();
    }

    @Transactional
    public void confirmRegistration(CodeConfirmationRequest request) {
        // Ищем пользователя
        UserEntity user = userRepository.findById(request.getId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким ID не найден"));

        // Проверяем, не активирован ли он уже
        if (user.isActive()) {
            throw new UserAlreadyExistsException("Аккаунт уже подтвержден. Повторная активация не требуется");
        }

        // Ищем код в таблице temp_code
        TempCodeEntity tempCode = (TempCodeEntity) tempCodeRepository.findByUser(user)
                .orElseThrow(() -> new InvalidCodeException("Код подтверждения не найден"));

        // Проверяем совпадение кода
        if (!tempCode.getCode().equals(request.getCode())) {
            throw new InvalidCodeException("Неверный код подтверждения");
        }

        // Активируем пользователя
        user.setActive(true);
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);

        // Удаляем временный код
        tempCodeRepository.delete(tempCode);
    }
}
