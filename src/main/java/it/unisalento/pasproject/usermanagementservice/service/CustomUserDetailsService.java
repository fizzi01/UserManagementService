package it.unisalento.pasproject.usermanagementservice.service;

import it.unisalento.pasproject.usermanagementservice.domain.User;
import it.unisalento.pasproject.usermanagementservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


// Questa classe implementa l'interfaccia UserDetailsService di Spring Security, fornisce dettagli di un certo utente
@Service
public class CustomUserDetailsService {

    @Autowired
    UserRepository userRepository;

    public User loadUserByUsername(String email) throws UsernameNotFoundException {

        final User user = userRepository.findByEmail(email);

        if(user == null) {
            throw new UsernameNotFoundException(email);
        }

        return user;
    }
}
