package it.unisalento.pasproject.usermanagementservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserListDTO {
    private List<UserDTO> usersList;

    public UserListDTO() {
        this.usersList = new ArrayList<>();
    }
}
