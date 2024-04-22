package it.unisalento.pasproject.usermanagementservice.service;

import it.unisalento.pasproject.usermanagementservice.domain.User;
import it.unisalento.pasproject.usermanagementservice.dto.UserDTO;
import it.unisalento.pasproject.usermanagementservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Get all users.
     * @return List of all users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get a user by email.
     * @param email The email of the user.
     * @return User object containing the user details.
     */
    public User getUserByEmail(String email) {
        //Ricerca utenti abilitati
        return userRepository.findByEmailAndEnabled(email,true);
    }

    /**
     * Create a new user.
     * @param user The User object to be created.
     * @return The created User object.
     */
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Update an existing user.
     * @param id The id of the user to be updated.
     * @param user The User object containing the updated details.
     * @return The updated User object.
     */
    public User updateUser(String id, User user) {
        // Implement update logic here
        return userRepository.save(user);
    }

    /**
     * Delete a user.
     * @param id The id of the user to be deleted.
     */
    public void deleteUser(String id) {
        //TODO: Aggiorna il campo enable dell'utente
    }


    /**
     * Find users based on provided filters.
     * @param email The email of the user.
     * @param name The name of the user.
     * @param surname The surname of the user.
     * @param role The role of the user.
     * @param enabled The enabled status of the user.
     * @return List of users that match the filters.
     */
    public List<User> findUsers(String email, String name, String surname, String role, Boolean enabled) {
        Query query = new Query();

        if (email != null) {
            query.addCriteria(Criteria.where("email").is(email));
        }
        if (name != null) {
            query.addCriteria(Criteria.where("name").is(name));
        }
        if (surname != null) {
            query.addCriteria(Criteria.where("surname").is(surname));
        }
        if (role != null) {
            query.addCriteria(Criteria.where("role").is(role));
        }
        if(enabled != null){
            query.addCriteria(Criteria.where("enabled").is(enabled));
        } else {
            query.addCriteria(Criteria.where("enabled").is(true));
        }

        return mongoTemplate.find(query, User.class);
    }

    /**
     * Convert a User domain object to a UserDTO.
     * @param user The User object to be converted.
     * @return The converted UserDTO.
     */
    public UserDTO domainToDto(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setSurname(user.getSurname());
        userDTO.setRole(user.getRole());
        return userDTO;
    }
}
