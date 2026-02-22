package ru.cinimex.userimpl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.cinimex.userapi.dto.NotificationMessage;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    public void sendRegistrationCode(String email, String code) {
        NotificationMessage message = NotificationMessage.builder()
                .email(email)
                .header("Подтверждение почты")
                .body("Ваш код подтверждения - " + code)
                .build();

        kafkaTemplate.send(topic, message);
    }
}
