package com.eduhi.bookstore.controller;

import com.eduhi.bookstore.dto.UserRecordDto;
import com.eduhi.bookstore.exception.NotFoundException;
import com.eduhi.bookstore.models.UserEntity;
import com.eduhi.bookstore.services.UserService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {


    final UserService userService;

    public UserController (UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserEntity> saveUser(@RequestBody @Valid UserRecordDto userRecordDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.save(userRecordDto));
    }

    @PreAuthorize("#id == authentication.principal.userID or hasRole('ADMIN')")
    @GetMapping("/")
    public ResponseEntity<UserEntity> getUserById(@RequestParam(value = "user-id") UUID id) throws NotFoundException {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getUserById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getAllUsers());
    }
}
