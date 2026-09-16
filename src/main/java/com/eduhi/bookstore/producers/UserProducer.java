package com.eduhi.bookstore.producers;

import com.eduhi.bookstore.dto.EmailDto;
import com.eduhi.bookstore.models.UserModel;
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

    public void publishMessageEmail(UserModel userModel) {
        var emailDto = new EmailDto();
        emailDto.setUserId(userModel.getUserID());
        emailDto.setEmailTo(userModel.getEmail());
        emailDto.setSubject("Sign up complete!");
        emailDto.setText(
                userModel.getName() + ", welcome to our library! \n" +
                "Thank you for your interest in our bookstore.");

        // Empty string for default exchange
        rabbitTemplate.convertAndSend("", routingKey, emailDto);
    }
}
