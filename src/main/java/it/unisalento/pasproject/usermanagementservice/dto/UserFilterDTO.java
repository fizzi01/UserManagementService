package it.unisalento.pasproject.usermanagementservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFilterDTO {
        private String email;
        private String name;
        private String surname;
        private String role;
        private Boolean enabled;
}
