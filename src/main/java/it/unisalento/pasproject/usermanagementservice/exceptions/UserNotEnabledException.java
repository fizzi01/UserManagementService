package it.unisalento.pasproject.usermanagementservice.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "User not enabled")
public class UserNotEnabledException extends Exception{
}
