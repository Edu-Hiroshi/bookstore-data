package com.eduhi.bookstore.services;

import com.eduhi.bookstore.dto.UserRecordDto;
import com.eduhi.bookstore.models.UserEntity;
import com.eduhi.bookstore.producers.UserProducer;
import com.eduhi.bookstore.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
}
