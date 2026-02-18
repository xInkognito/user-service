package ru.cinimex.userapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cinimex.userapi.dto.*;

import java.time.OffsetDateTime;

@Tag(name = "Admin Controller")
@RequestMapping(path = "/admin")
public interface AdminController {

    @Operation(
            summary = "Получение информации о пользователе по логину",
            description = "Доступно пользователям с ролями: ADMIN, TECH"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о пользователе успешно получена",
                    content = @Content(schema = @Schema(implementation = UserInformationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Токен не передан (Unauthorized)", content = @Content),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (нужна роль ADMIN или TECH)", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping(value = "/users/{login}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserInformationResponse> getUserByLogin(@PathVariable String login);

    @Operation(
            summary = "Генерация технического токена",
            description = "Генерирует JWT для технического пользователя, который не сохраняется в БД. Доступно только для роли: ADMIN"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Технический токен успешно сгенерирован",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Токен не передан (Unauthorized)", content = @Content),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав (нужна роль ADMIN)", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PostMapping(value = "/tech/token", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TokenResponse> generateTechToken(@RequestParam OffsetDateTime expirationDate);
}