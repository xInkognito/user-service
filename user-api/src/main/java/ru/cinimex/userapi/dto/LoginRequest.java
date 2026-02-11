package ru.cinimex.userapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Данные для аутентификации пользователя")
public class LoginRequest {

    @Schema(description = "Логин пользователя", example = "inkognito")
    private String username;

    private String password;
}
