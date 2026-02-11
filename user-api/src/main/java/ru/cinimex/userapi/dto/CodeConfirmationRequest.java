package ru.cinimex.userapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Данные для подтверждения регистрации через код")
public class CodeConfirmationRequest {

    @Schema(description = "Идентификатор пользователя", example = "7245f92e-6cc3-4220-bf80-7369ee2e1011")
    private UUID id;

    @Schema(description = "Код подтверждения", example = "546743")
    private String code;
}
