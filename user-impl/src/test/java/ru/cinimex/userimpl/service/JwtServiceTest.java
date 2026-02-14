package ru.cinimex.userimpl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    private JwtService jwtService;
    private final String SECRET = "mySecretKeyForTestingPurposesOnly12345678";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Вручную устанавливаем значение приватного поля jwtSecret
        ReflectionTestUtils.setField(jwtService, "jwtSecret", SECRET);
    }

    @Test
    void generateToken_ShouldReturnValidToken_WhenUserIsAuthenticated() {
        // Создаем мок объекта Authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        User user = new User("test_user", "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(authentication.getPrincipal()).thenReturn(user);

        String token = jwtService.generateToken(authentication);

        assertNotNull(token);
        assertEquals("test_user", jwtService.extractUserName(token));
        assertTrue(jwtService.extractRole(token).contains("ROLE_USER"));
    }

    @Test
    void generateTechToken_ShouldReturnValidToken() {
        OffsetDateTime expiry = OffsetDateTime.now().plusHours(1);

        String token = jwtService.generateTechToken(expiry);

        assertNotNull(token);
        assertEquals("tech_user", jwtService.extractUserName(token));
        assertTrue(jwtService.extractRole(token).contains("ROLE_TECH"));
    }

    @Test
    void extractUserName_ShouldReturnCorrectSubject() {
        OffsetDateTime expiry = OffsetDateTime.now().plusHours(1);
        String token = jwtService.generateTechToken(expiry);

        String username = jwtService.extractUserName(token);

        assertEquals("tech_user", username);
    }

    @Test
    void extractExpiration_ShouldReturnFutureDate() {
        OffsetDateTime expiry = OffsetDateTime.now().plusHours(1);
        String token = jwtService.generateTechToken(expiry);

        Date expirationDate = jwtService.extractExpiration(token);

        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void extractRole_ShouldReturnRolesList() {
        OffsetDateTime expiry = OffsetDateTime.now().plusHours(1);
        String token = jwtService.generateTechToken(expiry);

        List<String> roles = jwtService.extractRole(token);

        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertEquals("ROLE_TECH", roles.get(0));
    }
}