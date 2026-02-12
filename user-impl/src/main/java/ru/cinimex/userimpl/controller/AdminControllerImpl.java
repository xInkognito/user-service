package ru.cinimex.userimpl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import ru.cinimex.userapi.controller.AdminController;
import ru.cinimex.userapi.dto.UserInformationResponse;

import java.time.OffsetDateTime;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminControllerImpl implements AdminController {

    @Override
    public ResponseEntity<UserInformationResponse> getUserByLogin(String login) {
        return null;
    }

    @Override
    public ResponseEntity<String> generateTechToken(OffsetDateTime expirationDate) {
        return null;
    }
}
