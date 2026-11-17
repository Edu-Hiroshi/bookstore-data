package com.eduhi.bookstore.producers;

import com.eduhi.bookstore.dto.EmailDto;
import com.eduhi.bookstore.models.UserEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserProducer {

    final RabbitTemplate rabbitTemplate;

    public UserProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // default exchange: routing key == queue name
    @Value(value = "${broker.queue.email.name}")
    private String routingKey;

    public void publishMessageEmail(UserEntity userEntity) {
        var emailDto = new EmailDto();
        emailDto.setUserId(userEntity.getUserID());
        emailDto.setEmailTo(userEntity.getEmail());
        emailDto.setSubject("Sign up complete!");
        emailDto.setText(
                userEntity.getName() + ", welcome to our library! \n" +
                "Thank you for your interest in our bookstore.");

        // Empty string for default exchange
        rabbitTemplate.convertAndSend("", routingKey, emailDto);
    }
}
