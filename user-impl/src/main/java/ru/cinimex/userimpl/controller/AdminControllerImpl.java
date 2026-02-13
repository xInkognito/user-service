package ru.cinimex.userimpl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import ru.cinimex.userapi.controller.AdminController;
import ru.cinimex.userapi.dto.TokenResponse;
import ru.cinimex.userapi.dto.UserInformationResponse;
import ru.cinimex.userimpl.service.JwtService;
import ru.cinimex.userimpl.service.UserService;

import java.time.OffsetDateTime;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class AdminControllerImpl implements AdminController {

    private final UserService userService;
    private final JwtService jwtService;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'TECH')")
    public ResponseEntity<UserInformationResponse> getUserByLogin(String login) {
        UserInformationResponse response = userService.getUserInfo(login);
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TokenResponse> generateTechToken(OffsetDateTime expirationDate) {
        return ResponseEntity.ok(new TokenResponse(jwtService.generateTechToken(expirationDate)));
    }
}