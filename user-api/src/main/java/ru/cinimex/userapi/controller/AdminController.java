package ru.cinimex.userapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cinimex.userapi.dto.*;

import java.time.OffsetDateTime;

@Tag(name = "Admin Controller")
@RequestMapping(path = "/admin")
public interface AdminController {

    @GetMapping(value = "/users/{login}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<UserInformationResponse> getUserByLogin(@PathVariable String login);

    // Здесь нет @RequestBody, данные идут в Query-параметрах, поэтому consumes не нужен
    @PostMapping(value = "/tech/token", produces = MediaType.TEXT_PLAIN_VALUE)
    ResponseEntity<String> generateTechToken(@RequestParam OffsetDateTime expirationDate);
}