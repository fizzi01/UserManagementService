package it.unisalento.pasproject.usermanagementservice.service;

import it.unisalento.pasproject.usermanagementservice.domain.User;
import it.unisalento.pasproject.usermanagementservice.exceptions.UserNotFoundException;
import it.unisalento.pasproject.usermanagementservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static it.unisalento.pasproject.usermanagementservice.security.SecurityConstants.ROLE_ADMIN;


@Service
public class UserCheckService {
    private final UserRepository userRepository;

    @Autowired
    public UserCheckService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User loadUserByUsername(String email) throws UsernameNotFoundException {

        final User user = userRepository.findByEmail(email);

        if(user == null) {
            throw new UserNotFoundException(email);
        }

        return user;
    }

    public Boolean isEnable(Boolean enable) {
        return enable;
    }

    /**
     * Check if the current user is the user with the given email
     * @param email the email of the user to check
     * @return true if the current user is the user with the given email, false otherwise
     */
    public Boolean isCorrectUser(String email){
        return email.equals(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    /**
     * Get the email of the current user
     * @return the email of the current user
     */
    public String getCurrentUserEmail(){
        return ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }

    /**
     * Check if the current user is an administrator
     * @return true if the current user is an administrator, false otherwise
     */
    public Boolean isAdministrator(){
        String currentRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        return currentRole.equalsIgnoreCase(ROLE_ADMIN);
    }
}
