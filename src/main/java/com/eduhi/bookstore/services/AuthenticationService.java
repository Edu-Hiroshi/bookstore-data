package com.eduhi.bookstore.services;

import com.eduhi.bookstore.dto.RegisterRequestDto;
import com.eduhi.bookstore.enums.RoleTypeEnum;
import com.eduhi.bookstore.models.RolesEntity;
import com.eduhi.bookstore.models.UserEntity;
import com.eduhi.bookstore.producers.UserProducer;
import com.eduhi.bookstore.repositories.RolesRepository;
import com.eduhi.bookstore.repositories.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository, RolesRepository rolesRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity Register(RegisterRequestDto dto) throws BadRequestException {
        UserEntity user = userRepository.findByEmail(dto.email())
                .orElse(null);

        if (user != null) {
            throw new BadRequestException("User already registered.");
        }

        RolesEntity role = rolesRepository.findByRoleName(RoleTypeEnum.STUDENT.name())
                .orElseGet(() -> {
                    var newRole = new RolesEntity();
                    newRole.setRoleName(RoleTypeEnum.STUDENT.name());
                    rolesRepository.save(newRole);
                    return newRole;
                });

        Instant now = Instant.now();
        var userModel = new UserEntity();

        userModel.setEmail(dto.email());
        userModel.setName(dto.name());
        userModel.setRoles(Set.of(role));
        userModel.setPassword(passwordEncoder.encode(dto.password()));
        userModel.setCreated(now);
        userModel.setUpdated(now);
        userModel = userRepository.save(userModel);

        return userModel;
    }
}
