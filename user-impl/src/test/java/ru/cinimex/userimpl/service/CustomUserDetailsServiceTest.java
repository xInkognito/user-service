package ru.cinimex.userimpl.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.cinimex.userimpl.domain.UserEntity;
import ru.cinimex.userimpl.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("Успешная загрузка пользователя по имени")
    void loadUserByUsername_Success() {
        String username = "ivan_ivanov";
        UserEntity mockEntity = new UserEntity();
        mockEntity.setUsername(username);
        mockEntity.setPassword("encoded_password");
        mockEntity.setRole("ADMIN");
        mockEntity.setActive(true);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockEntity));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("encoded_password", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());

        // Проверяем, что роль получила префикс ROLE_
        boolean hasAdminRole = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        assertTrue(hasAdminRole, "Пользователь должен иметь роль ROLE_ADMIN");

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("Выброс исключения, если пользователь не найден")
    void loadUserByUsername_UserNotFound_ThrowsException() {
        String username = "ghost_user";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });

        verify(userRepository, times(1)).findByUsername(username);
    }
}