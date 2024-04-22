package it.unisalento.pasproject.usermanagementservice.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "user")
public class User {
    // Contiene l'id dell utente, email, name e surname, ruolo, se abilitato o meno, la password e il numero di crediti
    @Id
    private String id;
    private String email;
    private String name;
    private String surname;
    private String role;
    private boolean enabled;
}
