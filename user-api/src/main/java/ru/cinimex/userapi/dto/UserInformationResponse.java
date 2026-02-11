package ru.cinimex.userapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ с информацией о пользователя")
public class UserInformationResponse {

    @Schema(description = "Логин пользователя", example = "inkognito")
    private String username;

    @Schema(description = "Почта пользователя", example = "inkognito@cinimex.ru")
    private String email;

    @Schema(description = "Роль пользователя", example = "USER")
    private String role;
}
