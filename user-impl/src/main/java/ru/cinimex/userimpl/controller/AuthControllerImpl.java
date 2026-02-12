package ru.cinimex.userimpl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import ru.cinimex.userapi.controller.AuthController;
import ru.cinimex.userapi.dto.*;
import ru.cinimex.userimpl.exception.UserAlreadyExistsException;
import ru.cinimex.userimpl.service.JwtService;
import ru.cinimex.userimpl.service.RegistrationService;

import java.util.UUID;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RegistrationService registrationService;

    @Override
    public ResponseEntity<TokenResponse> login(LoginRequest loginRequest) {
        return null;
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
        return null;
    }
}
