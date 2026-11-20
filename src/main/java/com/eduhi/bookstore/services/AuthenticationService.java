package com.eduhi.bookstore.services;

import com.eduhi.bookstore.configs.TokenProvider;
import com.eduhi.bookstore.dto.LoginRequestDto;
import com.eduhi.bookstore.dto.RegisterRequestDto;
import com.eduhi.bookstore.dto.TokenResponseDto;
import com.eduhi.bookstore.enums.RoleTypeEnum;
import com.eduhi.bookstore.exception.BadRequestException;
import com.eduhi.bookstore.models.RolesEntity;
import com.eduhi.bookstore.models.UserEntity;
import com.eduhi.bookstore.repositories.RolesRepository;
import com.eduhi.bookstore.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
public class AuthenticationService {

    @Value("${jwt.expiration}")
    private long expirationTime;

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    public AuthenticationService(UserRepository userRepository, RolesRepository rolesRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public void register(RegisterRequestDto dto) throws BadRequestException {
        UserEntity user = userRepository.findByEmail(dto.email())
                .orElse(null);

        if (user != null) {
            throw new BadRequestException("User already registered.");
        }

        RolesEntity role = rolesRepository.findByRoleName(RoleTypeEnum.ROLE_STUDENT.name())
                .orElseGet(() -> {
                    var newRole = new RolesEntity();
                    newRole.setRoleName(RoleTypeEnum.ROLE_STUDENT.name());
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
        userRepository.save(userModel);
    }

    public TokenResponseDto login(LoginRequestDto dto) throws Exception{
        try {
            // Auth manager calls auth provider, in turn it will call UserDetailService
            // to find the user, matching the password with the encoded password.
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(dto.email(),
                                                                          dto.password()));
            String token = tokenProvider.generateToken(authentication);

            return new TokenResponseDto(token, expirationTime);
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid credentials.");
        }
    }

}
