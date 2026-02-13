package ru.cinimex.userimpl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import ru.cinimex.userapi.controller.AuthController;
import ru.cinimex.userapi.dto.*;
import ru.cinimex.userimpl.service.UserService;
import ru.cinimex.userimpl.service.RegistrationService;

import java.util.UUID;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final RegistrationService registrationService;
    private final UserService userService;

    @Override
    public ResponseEntity<TokenResponse> login(LoginRequest loginRequest) {
        String accessToken = userService.loginUser(loginRequest);
        return ResponseEntity.ok(new TokenResponse(accessToken));
    }

    @Override
    public ResponseEntity<UUID> register(RegisterRequest registerRequest) {
        UUID userId = registrationService.registerUser(registerRequest);
        return ResponseEntity.ok(userId);
    }

    @Override
    public ResponseEntity<Void> confirmCode(CodeConfirmationRequest codeConfirmationRequest) {
        registrationService.confirmRegistration(codeConfirmationRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserInformationResponse> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username;
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }

        UserInformationResponse response = userService.getUserInfo(username);
        return ResponseEntity.ok(response);
    }
}
