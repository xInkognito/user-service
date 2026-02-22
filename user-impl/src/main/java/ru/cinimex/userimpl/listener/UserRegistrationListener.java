package ru.cinimex.userimpl.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.cinimex.userimpl.event.UserRegisteredEvent;
import ru.cinimex.userimpl.service.KafkaProducerService;

@Component
@RequiredArgsConstructor
public class UserRegistrationListener {

    private final KafkaProducerService kafkaProducerService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        kafkaProducerService.sendRegistrationCode(event.getEmail(), event.getCode());
    }
}