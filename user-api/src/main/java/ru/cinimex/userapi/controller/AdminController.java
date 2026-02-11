package ru.cinimex.userapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cinimex.userapi.dto.*;

import java.time.OffsetDateTime;

@RequestMapping("/admin")
public interface AdminController {

    @GetMapping("/users/{login}")
    ResponseEntity<UserInformationResponse> getUserByLogin(@PathVariable String login);

    @PostMapping("/tech/token")
    ResponseEntity<String> generateTechToken(@RequestParam OffsetDateTime expirationDate);
}
