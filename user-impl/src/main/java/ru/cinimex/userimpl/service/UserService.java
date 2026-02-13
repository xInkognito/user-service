package ru.cinimex.userimpl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.cinimex.userapi.dto.LoginRequest;
import ru.cinimex.userapi.dto.UserInformationResponse;
import ru.cinimex.userimpl.domain.UserEntity;
import ru.cinimex.userimpl.exception.UserNotFoundException;
import ru.cinimex.userimpl.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public String loginUser(LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getUsername(),
                        req.getPassword()
                )
        );

        return jwtService.generateToken(authentication);
    }

    public UserInformationResponse getUserInfo(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        return UserInformationResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
