package ru.cinimex.userapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Данные для регистрации нового пользователя")
public class RegisterRequest {

    @Schema(description = "Логин пользователя", example = "inkognito")
    private String username;

    @Schema(description = "Пароль пользователя", example = "StrongPassword123", type = "string", format = "password")
    private String password;

    @Schema(description = "Почта пользователя", example = "inkognito@cinimex.ru")
    private String email;
}