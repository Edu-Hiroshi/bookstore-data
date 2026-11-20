package com.eduhi.bookstore.services;

import com.eduhi.bookstore.dto.UserRecordDto;
import com.eduhi.bookstore.exception.NotFoundException;
import com.eduhi.bookstore.models.AuthorEntity;
import com.eduhi.bookstore.models.UserEntity;
import com.eduhi.bookstore.producers.UserProducer;
import com.eduhi.bookstore.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    final UserRepository userRepository;
    final UserProducer userProducer;

    public UserService(UserRepository userRepository, UserProducer userProducer) {
        this.userRepository = userRepository;
        this.userProducer = userProducer;
    }

    @Transactional
    public UserEntity save(UserRecordDto userRecordDto) {
        Instant now = Instant.now();
        var userModel = new UserEntity();

        userModel.setEmail(userRecordDto.email());
        userModel.setName(userRecordDto.name());
        userModel.setCreated(now);
        userModel.setUpdated(now);
        userModel = userRepository.save(userModel);

        userProducer.publishMessageEmail(userModel);
        return userModel;
    }

    public UserEntity getUserById(UUID id) throws NotFoundException {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("User not found."));
    }

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
}
