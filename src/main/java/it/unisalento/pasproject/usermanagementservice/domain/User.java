package it.unisalento.pasproject.usermanagementservice.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Document(collection = "user")
public class User {

    @Id
    private String id;
    private String email;
    private String name;
    private String surname;
    private String role;
    private Date registrationDate;
    private Boolean enabled;
}
