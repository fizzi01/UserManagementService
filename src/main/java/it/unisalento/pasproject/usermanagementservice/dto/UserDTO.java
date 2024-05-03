package it.unisalento.pasproject.usermanagementservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserDTO {
    private String email;
    private String name;
    private String surname;
    private Date registrationDate;
    private String role;
}
