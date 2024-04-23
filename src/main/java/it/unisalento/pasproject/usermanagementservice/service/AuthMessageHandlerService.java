package it.unisalento.pasproject.usermanagementservice.service;

import it.unisalento.pasproject.usermanagementservice.domain.User;
import it.unisalento.pasproject.usermanagementservice.dto.UserDTO;
import it.unisalento.pasproject.usermanagementservice.repositories.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthMessageHandlerService {

    private final UserService userService;

    @Autowired
    public AuthMessageHandlerService(UserService userService) {
        this.userService = userService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.data.name}")
    public void receiveMessage(UserDTO userDTO) {
        // Convert UserDTO to User
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setRole(userDTO.getRole());
        user.setEnabled(true);

        // Save User to MongoDB
        userService.createUser(user);
    }

}