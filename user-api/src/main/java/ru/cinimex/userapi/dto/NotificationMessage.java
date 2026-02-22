package ru.cinimex.userapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationMessage {
    private String email;
    private String header;
    private String body;
}
