package ru.cinimex.userimpl.controller;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.cinimex.userapi.dto.UserInformationResponse;
import ru.cinimex.userimpl.exception.UserNotFoundException;
import ru.cinimex.userimpl.filter.JwtFilter;
import ru.cinimex.userimpl.listener.UserRegistrationListener;
import ru.cinimex.userimpl.repository.UserRepository;
import ru.cinimex.userimpl.service.JwtService;
import ru.cinimex.userimpl.service.KafkaProducerService;
import ru.cinimex.userimpl.service.RegistrationService;
import ru.cinimex.userimpl.service.UserService;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
class AdminControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private RegistrationService registrationService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private KafkaProducerService kafkaProducerService;

    @Nested
    @DisplayName("GET /admin/users/{login}")
    class GetUserByLoginTests {

        @Test
        @DisplayName("200 OK: Успех для ADMIN")
        @WithMockUser(roles = "ADMIN")
        void successForAdmin() throws Exception {
            UserInformationResponse response = new UserInformationResponse();
            response.setUsername("ivan");
            response.setEmail("ivan@gmail.com");
            response.setRole("USER");

            when(userService.getUserInfo("ivan")).thenReturn(response);

            mockMvc.perform(get("/admin/users/ivan"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("ivan"))
                    .andExpect(jsonPath("$.email").value("ivan@gmail.com"))
                    .andExpect(jsonPath("$.role").value("USER"));
        }

        @Test
        @DisplayName("200 OK: Успех для TECH")
        @WithMockUser(roles = "TECH")
        void successForTech() throws Exception {
            when(userService.getUserInfo("tech_bot")).thenReturn(new UserInformationResponse());

            mockMvc.perform(get("/admin/users/tech_bot"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("403 Forbidden: Роль USER не имеет доступа")
        @WithMockUser(roles = "USER")
        void forbiddenForUserRole() throws Exception {
            mockMvc.perform(get("/admin/users/any"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("401 Unauthorized: Токен не передан")
        void unauthorizedWhenNoToken() throws Exception {
            mockMvc.perform(get("/admin/users/any"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("400 Bad Request: Пользователь не найден в системе")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn400WhenUserNotFound() throws Exception {
            String login = "unknown_user";
            String errorMessage = "Пользователь с логином unknown_user не найден";

            when(userService.getUserInfo(login))
                    .thenThrow(new UserNotFoundException(errorMessage));

            mockMvc.perform(get("/admin/users/" + login))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(errorMessage));
        }

        @Test
        @DisplayName("500 Internal Server Error: Ошибка в сервисе")
        @WithMockUser(roles = "ADMIN")
        void serverError() throws Exception {
            when(userService.getUserInfo(any())).thenThrow(new RuntimeException("DB is down"));

            mockMvc.perform(get("/admin/users/any"))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("POST /admin/tech/token")
    class GenerateTechTokenTests {

        @Test
        @DisplayName("200 OK: ADMIN может генерировать токен")
        @WithMockUser(roles = "ADMIN")
        void successForAdmin() throws Exception {
            String mockToken = "super-secret-tech-token";
            when(jwtService.generateTechToken(any(OffsetDateTime.class))).thenReturn(mockToken);

            mockMvc.perform(post("/admin/tech/token")
                            .param("expirationDate", "2026-12-31T23:59:59Z"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value(mockToken));
        }

        @Test
        @DisplayName("403 Forbidden: TECH не может генерировать токен (нужен ADMIN)")
        @WithMockUser(roles = "TECH")
        void forbiddenForTechRole() throws Exception {
            mockMvc.perform(post("/admin/tech/token")
                            .param("expirationDate", "2026-12-31T23:59:59Z"))
                    .andExpect(status().isForbidden());
        }
    }
}