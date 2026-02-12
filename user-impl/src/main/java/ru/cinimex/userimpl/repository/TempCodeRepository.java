package ru.cinimex.userimpl.repository;

import org.springframework.data.repository.CrudRepository;
import ru.cinimex.userimpl.domain.TempCodeEntity;
import ru.cinimex.userimpl.domain.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface TempCodeRepository extends CrudRepository<TempCodeEntity, UUID> {
    Optional<Object> findByUser(UserEntity user);
}
