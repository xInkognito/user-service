package ru.cinimex.userapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.cinimex.userapi.dto.*;

import java.util.UUID;

@Tag(name = "Auth Controller", description = "Регистрация и аутентификация")
public interface AuthController {

    @Operation(summary = "Аутентификация и получение JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "403", description = "Неправильный логин или пароль"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/auth/login")
    ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest);

    @Operation(summary = "Регистрация нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Регистрация успешно пройдена, возвращен id"),
            @ApiResponse(responseCode = "400", description = "Пользователь уже существует"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping("/register")
    ResponseEntity<UUID> register(@RequestBody RegisterRequest registerRequest);

    @Operation(summary = "Подтверждение регистрации кодом")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Код подтвержден"),
            @ApiResponse(responseCode = "400", description = "Неверный код, пользователь уже активен или не найден"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping("/register/code")
    ResponseEntity<Void> confirmCode(@RequestBody CodeConfirmationRequest codeConfirmationRequest);

    @Operation(summary = "Получение информации о текущем пользователе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные получены",
                    content = @Content(schema = @Schema(implementation = UserInformationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Токен не передан или невалиден"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping("/users/current")
    ResponseEntity<UserInformationResponse> getCurrentUser();
}