package com.eduhi.bookstore.controller;

import com.eduhi.bookstore.dto.LoginRequestDto;
import com.eduhi.bookstore.dto.RegisterRequestDto;
import com.eduhi.bookstore.dto.TokenResponseDto;
import com.eduhi.bookstore.services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public void register(@RequestBody @Valid RegisterRequestDto registerRequestDto) throws Exception {
        authenticationService.register(registerRequestDto);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> register(@RequestBody @Valid LoginRequestDto loginRequestDto) throws Exception {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authenticationService.login(loginRequestDto));
    }
}
