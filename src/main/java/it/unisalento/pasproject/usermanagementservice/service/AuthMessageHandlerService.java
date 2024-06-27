package it.unisalento.pasproject.usermanagementservice.service;

import it.unisalento.pasproject.usermanagementservice.domain.User;
import it.unisalento.pasproject.usermanagementservice.domain.UserExtraInfo;
import it.unisalento.pasproject.usermanagementservice.dto.UpdatedProfileMessageDTO;
import it.unisalento.pasproject.usermanagementservice.dto.UserDTO;
import it.unisalento.pasproject.usermanagementservice.repositories.UserExtraInfoRepository;
import it.unisalento.pasproject.usermanagementservice.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static it.unisalento.pasproject.usermanagementservice.security.SecurityConstants.*;

@Service
public class AuthMessageHandlerService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserExtraInfoRepository userExtraInfoRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthMessageHandlerService.class);

    @Autowired
    public AuthMessageHandlerService(UserService userService, UserRepository userRepository, UserExtraInfoRepository userExtraInfoRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userExtraInfoRepository = userExtraInfoRepository;
    }

    @RabbitListener(queues = "${rabbitmq.queue.data.name}")
    public void receiveMessage(UserDTO userDTO) {
        // Convert UserDTO to User
        LOGGER.info("Received message: {}", userDTO.toString());
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(userDTO.getEmail()));

        if (optionalUser.isPresent()) {
            LOGGER.error("User already exists: {}", userDTO.getEmail());
        } else {
            User user = new User();
            user.setEmail(userDTO.getEmail());
            user.setName(userDTO.getName());
            user.setSurname(userDTO.getSurname());
            user.setRole(userDTO.getRole());
            user.setEnabled(true);
            user.setRegistrationDate(userDTO.getRegistrationDate());

            LOGGER.info("User domain: {}", user.toString());

            // Save User to MongoDB
            userService.createUser(user);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.update.name}")
    public void receiveUpdateMessage(UpdatedProfileMessageDTO updatedProfileMessageDTO) {
        try {
            LOGGER.info("Received message: {}", updatedProfileMessageDTO.toString());

            Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(updatedProfileMessageDTO.getEmail()));
            if(optionalUser.isPresent()) {
                User user = optionalUser.get();
                LOGGER.info("User found: {}", user.toString());

                Optional.ofNullable(updatedProfileMessageDTO.getName()).ifPresent(user::setName);
                Optional.ofNullable(updatedProfileMessageDTO.getSurname()).ifPresent(user::setSurname);
                Optional.of(updatedProfileMessageDTO.isEnabled()).ifPresent(user::setEnabled);
                userRepository.save(user);
                LOGGER.info("User saved: {}", user.toString());

                switch (updatedProfileMessageDTO.getRole()) {
                    case ROLE_MEMBRO -> {
                        UserExtraInfo userExtraInfo = userExtraInfoRepository.findByUserId(user.getId());
                        if (userExtraInfo == null) {
                            userExtraInfo = new UserExtraInfo();
                        }
                        Optional.ofNullable(user.getId()).ifPresent(userExtraInfo::setUserId);
                        Optional.ofNullable(updatedProfileMessageDTO.getResidenceCity()).ifPresent(userExtraInfo::setResidenceCity);
                        Optional.ofNullable(updatedProfileMessageDTO.getResidenceAddress()).ifPresent(userExtraInfo::setResidenceAddress);
                        Optional.ofNullable(updatedProfileMessageDTO.getPhoneNumber()).ifPresent(userExtraInfo::setPhoneNumber);
                        Optional.ofNullable(updatedProfileMessageDTO.getFiscalCode()).ifPresent(userExtraInfo::setFiscalCode);
                        Optional.ofNullable(updatedProfileMessageDTO.getBirthDate()).ifPresent(userExtraInfo::setBirthDate);
                        userExtraInfoRepository.save(userExtraInfo);
                        LOGGER.info("Extra info saved: {}", userExtraInfo);
                    }
                    case ROLE_UTENTE -> {
                        UserExtraInfo userExtraInfo = userExtraInfoRepository.findByUserId(user.getId());
                        if (userExtraInfo == null) {
                            userExtraInfo = new UserExtraInfo();
                        }
                        Optional.ofNullable(user.getId()).ifPresent(userExtraInfo::setUserId);
                        Optional.ofNullable(updatedProfileMessageDTO.getResidenceCity()).ifPresent(userExtraInfo::setResidenceCity);
                        Optional.ofNullable(updatedProfileMessageDTO.getResidenceAddress()).ifPresent(userExtraInfo::setResidenceAddress);
                        Optional.ofNullable(updatedProfileMessageDTO.getPhoneNumber()).ifPresent(userExtraInfo::setPhoneNumber);
                        Optional.ofNullable(updatedProfileMessageDTO.getFiscalCode()).ifPresent(userExtraInfo::setFiscalCode);
                        Optional.ofNullable(updatedProfileMessageDTO.getBirthDate()).ifPresent(userExtraInfo::setBirthDate);
                        Optional.ofNullable(updatedProfileMessageDTO.getCardNumber()).ifPresent(userExtraInfo::setCardNumber);
                        Optional.ofNullable(updatedProfileMessageDTO.getCardExpiryDate()).ifPresent(userExtraInfo::setCardExpiryDate);
                        Optional.ofNullable(updatedProfileMessageDTO.getCardCvv()).ifPresent(userExtraInfo::setCardCvv);
                        userExtraInfoRepository.save(userExtraInfo);
                        LOGGER.info("Extra info saved: {}", userExtraInfo);
                    }
                    default -> {}
                }
            } else {
                LOGGER.error("No user found with email: {}", updatedProfileMessageDTO.getEmail());
            }
        } catch (Exception e) {
            LOGGER.error("Error processing message: ", e);
        }
    }
}