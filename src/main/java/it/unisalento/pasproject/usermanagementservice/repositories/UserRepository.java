package it.unisalento.pasproject.usermanagementservice.repositories;

import it.unisalento.pasproject.usermanagementservice.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    //Repository per la gestione degli utenti
    //L'interfaccia UserRepository definisce i metodi per la gestione degli utenti
    //In particolare, un metodo per la ricerca di un utente per email
    //e un metodo per la verifica dell'esistenza di un utente
    //per email e password
    //I metodi sono implementati dalla classe UserRepositoryImpl
    //che si occupa di interagire con il database per eseguire le operazioni richieste
    /**
     * Cerca un utente per email.
     * @param email L'email dell'utente da cercare.
     * @return L'utente corrispondente all'email specificata.
     */
    User findByEmail(String email);

    /**
     * Cerca un utente per email e abilitato.
     * @param email L'email dell'utente da cercare.
     * @param enabled Lo stato di abilitazione dell'utente da cercare.
     * @return L'utente corrispondente all'email e abilitato specificati.
     */
    User findByEmailAndEnabled(String email, boolean enabled);

    //Metodi combinando ruolo ed enable
    /**
     * Cerca un utente per email, ruolo e abilitato.
     * @param email L'email dell'utente da cercare.
     * @param role Il ruolo dell'utente da cercare.
     * @param enabled Lo stato di abilitazione dell'utente da cercare.
     * @return L'utente corrispondente all'email, ruolo e abilitato specificati.
     */
    User findByEmailAndRoleAndEnabled(String email, String role, boolean enabled);

    /**
     * Verifica l'esistenza di un utente per email.
     * @param email L'email dell'utente da verificare.
     * @return True se l'utente esiste, False altrimenti.
     */
    boolean existsByEmailAndEnabled(String email, boolean enabled);


    /**
     * Verifica l'esistenza di un utente per email e password.
     * @param email L'email dell'utente da verificare.
     * @return True se l'utente esiste, False altrimenti.
     */
    boolean existsByEmail(String email);
}
