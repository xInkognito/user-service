package ru.cinimex.userapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ с UUID зарегистрированного пользователя")
public class RegistrationResponse {

    @Schema(
            description = "Идентификатор зарегистрированногопользователя",
            example = "7245f92e-6cc3-4220-bf80-7369ee2e1011"
    )
    private UUID generatedId;
}
