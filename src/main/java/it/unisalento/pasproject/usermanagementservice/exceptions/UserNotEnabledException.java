package it.unisalento.pasproject.usermanagementservice.exceptions;

import org.springframework.http.HttpStatus;

public class UserNotEnabledException extends CustomErrorException {
    public UserNotEnabledException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
