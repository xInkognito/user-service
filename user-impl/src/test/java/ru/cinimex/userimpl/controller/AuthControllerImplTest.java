package ru.cinimex.userimpl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.cinimex.userapi.dto.CodeConfirmationRequest;
import ru.cinimex.userapi.dto.LoginRequest;
import ru.cinimex.userapi.dto.RegisterRequest;
import ru.cinimex.userapi.dto.UserInformationResponse;
import ru.cinimex.userimpl.exception.UserAlreadyActivatedException;
import ru.cinimex.userimpl.exception.UserAlreadyExistsException;
import ru.cinimex.userimpl.exception.UserNotFoundException;
import ru.cinimex.userimpl.repository.UserRepository;
import ru.cinimex.userimpl.service.JwtService;
import ru.cinimex.userimpl.service.RegistrationService;
import ru.cinimex.userimpl.service.UserService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
class AuthControllerImplTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RegistrationService registrationService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @Nested
    @DisplayName("POST /auth/login")
    class LoginTests {

        @Test
        @DisplayName("200 OK: Успешный вход")
        void loginSuccess() throws Exception {
            LoginRequest request = new LoginRequest("ivan", "password123");
            when(userService.loginUser(any())).thenReturn("mocked-jwt-token");

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("mocked-jwt-token"));
        }

        @Test
        @DisplayName("403 Forbidden: Неверный логин или пароль (BadCredentialsException)")
        void shouldReturn403WhenCredentialsInvalid() throws Exception {
            LoginRequest request = new LoginRequest("user", "wrong_password");
            when(userService.loginUser(any())).thenThrow(new BadCredentialsException("Invalid info"));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Неверный логин или пароль"));
        }

        @Test
        @DisplayName("400 Bad Request: Аккаунт не активирован (DisabledException)")
        void shouldReturn400WhenAccountDisabled() throws Exception {
            LoginRequest request = new LoginRequest("inactive_user", "password");
            when(userService.loginUser(any())).thenThrow(new DisabledException("Not active"));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Аккаунт не активирован"));
        }
    }

    @Nested
    @DisplayName("POST /register")
    class RegisterTests {

        @Test
        @DisplayName("200 OK: Успешная регистрация")
        void registerSuccess() throws Exception {
            RegisterRequest request = new RegisterRequest();
            request.setUsername("new_user");
            UUID generatedId = UUID.randomUUID();

            when(registrationService.registerUser(any())).thenReturn(generatedId);

            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.generatedId").value(generatedId.toString()));
        }

        @Test
        @DisplayName("400 Bad Request: Пользователь уже существует")
        void registerUserAlreadyExists() throws Exception {
            RegisterRequest request = new RegisterRequest("existing_user", "existing@test.com", "pass");
            String errorMessage = "Пользователь с таким именем или email уже существует.";

            when(registrationService.registerUser(any()))
                    .thenThrow(new UserAlreadyExistsException(errorMessage));

            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(errorMessage));
        }
    }

    @Nested
    @DisplayName("POST /register/code")
    class ConfirmCodeTests {

        @Test
        @DisplayName("200 OK: Код подтвержден")
        void confirmCodeSuccess() throws Exception {
            CodeConfirmationRequest request = new CodeConfirmationRequest(UUID.randomUUID(), "123456");
            doNothing().when(registrationService).confirmRegistration(any());

            mockMvc.perform(post("/register/code")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("400 Bad Request: Пользователь не найден (UserNotFoundException)")
        void confirmCodeUserNotFound() throws Exception {
            CodeConfirmationRequest request = new CodeConfirmationRequest(UUID.randomUUID(), "123456");
            String errorMsg = "Пользователь с таким ID не найден";

            doThrow(new UserNotFoundException(errorMsg))
                    .when(registrationService).confirmRegistration(any());

            mockMvc.perform(post("/register/code")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(errorMsg));
        }

        @Test
        @DisplayName("400 Bad Request: Аккаунт уже активирован (UserAlreadyActivatedException)")
        void confirmCodeAlreadyActive() throws Exception {
            CodeConfirmationRequest request = new CodeConfirmationRequest(UUID.randomUUID(), "123456");
            String errorMsg = "Аккаунт уже подтвержден. Повторная активация не требуется";

            doThrow(new UserAlreadyActivatedException(errorMsg))
                    .when(registrationService).confirmRegistration(any());

            mockMvc.perform(post("/register/code")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(errorMsg));
        }
    }

    @Nested
    @DisplayName("GET /users/current")
    class GetCurrentUserTests {

        @Test
        @DisplayName("200 OK: Получение данных текущего пользователя (ADMIN)")
        @WithMockUser(username = "admin_user", roles = "ADMIN")
        void getCurrentUserAdminSuccess() throws Exception {
            String expectedName = "admin_user";

            UserInformationResponse response = UserInformationResponse.builder()
                    .username(expectedName)
                    .email("admin@cinimex.ru")
                    .role("ADMIN")
                    .build();

            when(userService.getUserInfo(expectedName)).thenReturn(response);

            mockMvc.perform(get("/users/current"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value(expectedName))
                    .andExpect(jsonPath("$.email").value("admin@cinimex.ru"))
                    .andExpect(jsonPath("$.role").value("ADMIN"));

            verify(userService).getUserInfo(expectedName);
        }

        @Test
        @DisplayName("200 OK: Получение данных текущего пользователя (USER)")
        @WithMockUser(username = "regular_user", roles = "USER")
        void getCurrentUserSuccess() throws Exception {
            String expectedName = "regular_user";

            UserInformationResponse response = new UserInformationResponse();
            response.setUsername(expectedName);

            when(userService.getUserInfo(expectedName)).thenReturn(response);

            mockMvc.perform(get("/users/current"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value(expectedName));

            verify(userService).getUserInfo(expectedName);
        }

        @Test
        @DisplayName("403 Forbidden: Роль TECH не имеет доступа к эндпоинту")
        @WithMockUser(username = "tech_bot", roles = "TECH")
        void getCurrentUserForbiddenForTech() throws Exception {
            mockMvc.perform(get("/users/current"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("401 Unauthorized: Пользователь не авторизован")
        void getCurrentUserUnauthorized() throws Exception {
            mockMvc.perform(get("/users/current"))
                    .andExpect(status().isUnauthorized());
        }
    }
}