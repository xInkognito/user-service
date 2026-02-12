package ru.cinimex.userimpl.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.cinimex.userapi.dto.RegisterRequest;
import ru.cinimex.userimpl.domain.UserEntity;

import java.time.OffsetDateTime;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public abstract class UserMapper {
    @Autowired
    protected PasswordEncoder passwordEncoder;

    public abstract UserEntity toEntity(RegisterRequest dto);

    @AfterMapping
    protected void postMappingSteps(RegisterRequest dto, @MappingTarget UserEntity entity) {
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setRole("USER");
        entity.setActive(false);
        entity.setCreatedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());
    }
}
