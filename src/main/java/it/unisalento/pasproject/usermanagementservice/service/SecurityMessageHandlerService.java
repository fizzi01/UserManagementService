package it.unisalento.pasproject.usermanagementservice.service;

import it.unisalento.pasproject.usermanagementservice.domain.User;
import it.unisalento.pasproject.usermanagementservice.dto.UserSecurityDTO;
import it.unisalento.pasproject.usermanagementservice.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecurityMessageHandlerService {
    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityMessageHandlerService.class);

    // Listener generico che processa richieste di qualsiasi tipo
    @RabbitListener(queues = "${rabbitmq.queue.security.name}")
    public UserSecurityDTO processRequest(String message) throws UserNotFoundException {
        LOGGER.info(String.format("Received message: %s", message));
        return handleRequest(message);
    }

    // Metodo per gestire la richiesta in maniera generica
    private UserSecurityDTO handleRequest(String email) throws UserNotFoundException {

        try {
            User user = userService.getUserByEmail(email);
            if (user == null) {
                throw new UserNotFoundException("User not found with email: " + email);
            }

            LOGGER.info(String.format("User %s found", email));

            UserSecurityDTO userSecurityDTO = new UserSecurityDTO();
            userSecurityDTO.setEmail(user.getEmail());
            userSecurityDTO.setRole(user.getRole());
            userSecurityDTO.setEnabled(user.getEnabled());

            LOGGER.info(String.format("User %s processed", userSecurityDTO));

            return userSecurityDTO;
        } catch (UserNotFoundException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

}
