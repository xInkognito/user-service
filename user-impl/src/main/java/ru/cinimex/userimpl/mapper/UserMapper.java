package ru.cinimex.userimpl.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.cinimex.userapi.dto.RegisterRequest;
import ru.cinimex.userimpl.domain.UserEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    @Autowired
    protected PasswordEncoder passwordEncoder;

    public abstract UserEntity toEntity(RegisterRequest dto);

    @AfterMapping
    protected void postMappingSteps(RegisterRequest dto, @MappingTarget UserEntity entity) {
        entity.setId(UUID.randomUUID());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setRole("USER");
        entity.setActive(false);

        OffsetDateTime now = OffsetDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
    }
}
