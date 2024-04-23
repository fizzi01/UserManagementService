package it.unisalento.pasproject.usermanagementservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSecurityDTO {
    private String email;
    private String role;
    private Boolean enabled;
}
