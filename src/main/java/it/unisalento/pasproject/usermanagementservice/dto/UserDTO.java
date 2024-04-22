package it.unisalento.pasproject.usermanagementservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String email;
    private String name;
    private String surname;
    private String role;
}
