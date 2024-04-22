package it.unisalento.pasproject.usermanagementservice.controller;

import it.unisalento.pasproject.usermanagementservice.domain.User;
import it.unisalento.pasproject.usermanagementservice.dto.UserDTO;
import it.unisalento.pasproject.usermanagementservice.dto.UserFilterDTO;
import it.unisalento.pasproject.usermanagementservice.dto.UserListDTO;
import it.unisalento.pasproject.usermanagementservice.exceptions.UserNotFoundException;
import it.unisalento.pasproject.usermanagementservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    @Autowired
    private UserService userService;

    /**
     * Get all users.
     * @return UserListDTO containing a list of all users.
     */
    @GetMapping
    public UserListDTO getAllUsers() {
        UserListDTO userListDTO = new UserListDTO();
        ArrayList<UserDTO> list = new ArrayList<>();
        userListDTO.setUsersList(list);

        List<User> users = userService.getAllUsers();
        for (User user : users) {
            list.add(userService.domainToDto(user));
        }

        return userListDTO;
    }

    /**
     * Get a user by email.
     * @param email The email of the user.
     * @return UserDTO containing the user details.
     * @throws UserNotFoundException if the user is not found.
     */
    @GetMapping("/{email}")
    public UserDTO getUserByEmail(@PathVariable String email) throws UserNotFoundException {
        User user = userService.getUserByEmail(email);
        if(user == null) {
            throw new UserNotFoundException();
        }

        return userService.domainToDto(user);
    }


    /**
     * Get users by filters.
     * @param email The email of the user.
     * @param name The name of the user.
     * @param surname The surname of the user.
     * @param role The role of the user.
     * @param enabled The enabled status of the user.
     * @return UserListDTO containing a list of users that match the filters.
     */
    @GetMapping("/find")
    public UserListDTO getByFilters(@RequestParam(required = false) String email,
                                    @RequestParam(required = false) String name,
                                    @RequestParam(required = false) String surname,
                                    @RequestParam(required = false) String role,
                                    @RequestParam(required = false) Boolean enabled){
        UserListDTO userListDTO = new UserListDTO();
        ArrayList<UserDTO> list = new ArrayList<>();
        userListDTO.setUsersList(list);


        List<User> users = userService.findUsers(email, name, surname, role, enabled);
        for (User user : users) {
            list.add(userService.domainToDto(user));
        }

        return userListDTO;
    }


    /**
     * Get users by filters using a UserFilterDTO.
     * @param filter The UserFilterDTO containing the filters.
     * @return UserListDTO containing a list of users that match the filters.
     */
    @PostMapping("/find")
    public UserListDTO getByFilters(@RequestBody UserFilterDTO filter) {
        UserListDTO userListDTO = new UserListDTO();
        ArrayList<UserDTO> list = new ArrayList<>();
        userListDTO.setUsersList(list);

        List<User> users = userService.findUsers(filter.getEmail(), filter.getName(), filter.getSurname(), filter.getRole(), filter.getEnabled());
        for (User user : users) {
            list.add(userService.domainToDto(user));
        }

        return userListDTO;
    }

}