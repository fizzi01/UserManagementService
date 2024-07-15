package it.unisalento.pasproject.usermanagementservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unisalento.pasproject.usermanagementservice.TestSecurityConfig;
import it.unisalento.pasproject.usermanagementservice.domain.User;
import it.unisalento.pasproject.usermanagementservice.domain.UserExtraInfo;
import it.unisalento.pasproject.usermanagementservice.dto.UserDTO;
import it.unisalento.pasproject.usermanagementservice.dto.UserFilterDTO;
import it.unisalento.pasproject.usermanagementservice.security.JwtUtilities;
import it.unisalento.pasproject.usermanagementservice.service.UserCheckService;
import it.unisalento.pasproject.usermanagementservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserManagementController.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Import(TestSecurityConfig.class)
public class UserManagementControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserCheckService userCheckService;

    @MockBean
    private JwtUtilities jwtUtilities;

    @InjectMocks
    private UserManagementController userManagementController;

    private static final String USER_EMAIL = "user1@example.com";
    private static final String ADMIN_EMAIL = "admin@example.com";
    private static final String NON_EXISTENT_EMAIL = "nonexistent@example.com";
    private static final String JWT_TOKEN = "Bearer 123456";

    List<User> users;

    @BeforeEach
    void setUp() {
        users = new ArrayList<>();

        User userOne = new User();
        userOne.setId("1");
        userOne.setEmail("user1@example.com");
        userOne.setRole("USER");
        userOne.setName("User One");
        userOne.setSurname("Surname One");
        userOne.setEnabled(true);

        User userTwo = new User();
        userTwo.setId("2");
        userTwo.setEmail("user2@example.com");
        userTwo.setRole("USER");
        userTwo.setName("User Two");
        userTwo.setSurname("Surname Two");
        userTwo.setEnabled(true);

        users.add(userOne);
        users.add(userTwo);
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getAllUsersWhenAdminShouldReturnListOfUsers() throws Exception {

        UserExtraInfo userExtraInfoOne = new UserExtraInfo();
        userExtraInfoOne.setUserId("1");
        userExtraInfoOne.setResidenceCity("CityOne");

        UserExtraInfo userExtraInfoTwo = new UserExtraInfo();
        userExtraInfoTwo.setUserId("2");
        userExtraInfoTwo.setResidenceCity("CityTwo");

        given(userService.getAllUsers()).willReturn(users);
        given(userService.getUserExtraInfoByUserId("1")).willReturn(userExtraInfoOne);
        given(userService.getUserExtraInfoByUserId("2")).willReturn(userExtraInfoTwo);

        given(userService.domainToDto(ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(user.getEmail());
            return userDTO;
        });

        given(userService.domainToDto(ArgumentMatchers.any(UserDTO.class), ArgumentMatchers.any(UserExtraInfo.class))).willAnswer(invocation -> {
            UserDTO userDTO = invocation.getArgument(0);
            UserExtraInfo userExtraInfo = invocation.getArgument(1);
            userDTO.setResidenceCity(userExtraInfo.getResidenceCity());
            return userDTO;
        });

        mockMvc.perform(get("/api/users")
                        .header("Authorization", JWT_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usersList", hasSize(2)))
                .andExpect(jsonPath("$.usersList[0].email", is("user1@example.com")))
                .andExpect(jsonPath("$.usersList[0].residenceCity", is("CityOne")))
                .andExpect(jsonPath("$.usersList[1].email", is("user2@example.com")))
                .andExpect(jsonPath("$.usersList[1].residenceCity", is("CityTwo")));
    }

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "UTENTE")
    void getAllUsersWhenNotAdminShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", JWT_TOKEN))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "UTENTE")
    void getUserByEmailWhenUserExistsAndAuthorizedShouldReturnUser() throws Exception {
        User userOne = users.getFirst();

        UserExtraInfo userExtraInfoOne = new UserExtraInfo();
        userExtraInfoOne.setUserId(userOne.getId());
        userExtraInfoOne.setResidenceCity("Test City");

        given(userCheckService.isAdministrator()).willReturn(false);
        given(userCheckService.isCorrectUser(USER_EMAIL)).willReturn(true);
        given(userService.getUserByEmail(USER_EMAIL)).willReturn(userOne);

        given(userService.domainToDto(ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(user.getEmail());
            return userDTO;
        });

        given(userService.getUserExtraInfoByUserId(userOne.getId())).willReturn(userExtraInfoOne);

        given(userService.domainToDto(ArgumentMatchers.any(UserDTO.class), ArgumentMatchers.any(UserExtraInfo.class))).willAnswer(invocation -> {
            UserDTO userDTO = invocation.getArgument(0);
            UserExtraInfo userExtraInfo = invocation.getArgument(1);
            userDTO.setResidenceCity(userExtraInfo.getResidenceCity());
            return userDTO;
        });

        mockMvc.perform(get("/api/users/{email}", USER_EMAIL)
                        .header("Authorization", JWT_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(USER_EMAIL)))
                .andExpect(jsonPath("$.residenceCity", is("Test City")));
    }

    @Test
    @WithMockUser(username = NON_EXISTENT_EMAIL, roles = "UTENTE")
    void getUserByEmailWhenUserDoesNotExistShouldReturnNotFound() throws Exception {
        given(userService.getUserByEmail(NON_EXISTENT_EMAIL)).willReturn(null);

        mockMvc.perform(get("/api/users/{email}", NON_EXISTENT_EMAIL)
                        .header("Authorization", JWT_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "UTENTE")
    void getUserByEmailWhenNotAuthorizedShouldReturnForbidden() throws Exception {
        given(userCheckService.isCorrectUser(NON_EXISTENT_EMAIL)).willReturn(false);
        given(userCheckService.isAdministrator()).willReturn(false);

        mockMvc.perform(get("/api/users/{email}", NON_EXISTENT_EMAIL)
                        .header("Authorization", JWT_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = NON_EXISTENT_EMAIL, roles = "ADMIN")
    void getUserByEmailAsAdminWhenUserDoesNotExistShouldReturnNotFound() throws Exception {
        given(userCheckService.isAdministrator()).willReturn(true);
        given(userCheckService.isCorrectUser(NON_EXISTENT_EMAIL)).willReturn(false);

        given(userService.getUserByEmail(NON_EXISTENT_EMAIL)).willReturn(null);

        mockMvc.perform(get("/api/users/{email}", NON_EXISTENT_EMAIL)
                        .header("Authorization", JWT_TOKEN))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = NON_EXISTENT_EMAIL, roles = "ADMIN")
    void getUserByEmailAsAdminShouldSucceedEvenIfEmailDoesNotMatch() throws Exception {
        User userOne = users.getFirst();

        UserExtraInfo userExtraInfoOne = new UserExtraInfo();
        userExtraInfoOne.setUserId(userOne.getId());
        userExtraInfoOne.setResidenceCity("CityOne");

        given(userCheckService.isAdministrator()).willReturn(true);
        given(userCheckService.isCorrectUser(NON_EXISTENT_EMAIL)).willReturn(false);

        given(userService.getUserByEmail(userOne.getEmail())).willReturn(userOne);

        given(userService.domainToDto(ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(user.getEmail());
            return userDTO;
        });

        given(userService.getUserExtraInfoByUserId(userOne.getId())).willReturn(userExtraInfoOne);

        given(userService.domainToDto(ArgumentMatchers.any(UserDTO.class), ArgumentMatchers.any(UserExtraInfo.class))).willAnswer(invocation -> {
            UserDTO userDTO = invocation.getArgument(0);
            UserExtraInfo userExtraInfo = invocation.getArgument(1);
            userDTO.setResidenceCity(userExtraInfo.getResidenceCity());
            return userDTO;
        });

        mockMvc.perform(get("/api/users/{email}", userOne.getEmail())
                        .header("Authorization", JWT_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("user1@example.com")))
                .andExpect(jsonPath("$.residenceCity", is("CityOne")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getByFiltersWithAllParametersShouldReturnFilteredUsers() throws Exception {
        User userOne = users.getFirst();

        UserExtraInfo userExtraInfoOne = new UserExtraInfo();
        userExtraInfoOne.setUserId("1");
        userExtraInfoOne.setResidenceCity("CityOne");

        List<User> filteredUsers = Collections.singletonList(userOne);

        given(userService.findUsers("user1@example.com", "User", "One", "USER", true)).willReturn(filteredUsers);

        given(userService.domainToDto(ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(user.getEmail());
            return userDTO;
        });

        given(userService.getUserExtraInfoByUserId("1")).willReturn(userExtraInfoOne);

        given(userService.domainToDto(ArgumentMatchers.any(UserDTO.class), ArgumentMatchers.any(UserExtraInfo.class))).willAnswer(invocation -> {
            UserDTO userDTO = invocation.getArgument(0);
            UserExtraInfo userExtraInfo = invocation.getArgument(1);
            userDTO.setResidenceCity(userExtraInfo.getResidenceCity());
            return userDTO;
        });

        mockMvc.perform(get("/api/users/find")
                        .param("email", "user1@example.com")
                        .param("name", "User")
                        .param("surname", "One")
                        .param("role", "USER")
                        .param("enabled", "true")
                        .header("Authorization", JWT_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usersList", hasSize(1)))
                .andExpect(jsonPath("$.usersList[0].email", is("user1@example.com")))
                .andExpect(jsonPath("$.usersList[0].residenceCity", is("CityOne")));
    }

    @Test
    @WithMockUser(username = ADMIN_EMAIL, roles = "ADMIN")
    void getByFiltersWithNoParametersShouldReturnAllUsers() throws Exception {
        given(userService.findUsers(null, null, null, null, null)).willReturn(users);

        given(userService.domainToDto(ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(user.getEmail());
            return userDTO;
        });

        given(userService.domainToDto(ArgumentMatchers.any(UserDTO.class), ArgumentMatchers.any(UserExtraInfo.class))).willAnswer(invocation -> {
            UserDTO userDTO = invocation.getArgument(0);
            UserExtraInfo userExtraInfo = invocation.getArgument(1);
            userDTO.setResidenceCity(userExtraInfo.getResidenceCity());
            return userDTO;
        });

        mockMvc.perform(get("/api/users/find")
                        .header("Authorization", JWT_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usersList", hasSize(2)));
    }

    @Test
    @WithMockUser(username = ADMIN_EMAIL, roles = "ADMIN")
    void getByFiltersWithInvalidRoleShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/users/find")
                        .param("role", "INVALID_ROLE")
                        .header("Authorization", JWT_TOKEN))
                .andExpect(jsonPath("$.usersList", hasSize(0)));
    }

    @Test
    @WithMockUser(username = USER_EMAIL, roles = "USER")
    void getByFiltersAsNonAdminShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/users/find")
                        .header("Authorization", JWT_TOKEN))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = ADMIN_EMAIL, roles = "ADMIN")
    void getByFiltersWithValidFiltersShouldReturnFilteredUsers() throws Exception {
        UserFilterDTO filter = new UserFilterDTO();
        filter.setEmail("user1@example.com");
        filter.setName("User");
        filter.setSurname("One");
        filter.setRole("USER");
        filter.setEnabled(true);

        List<User> filteredUsers = Collections.singletonList(users.getFirst());

        given(userService.findUsers(filter.getEmail(), filter.getName(), filter.getSurname(), filter.getRole(), filter.getEnabled())).willReturn(filteredUsers);

        given(userService.domainToDto(ArgumentMatchers.any(User.class))).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(user.getEmail());
            userDTO.setRole(user.getRole());
            return userDTO;
        });

        mockMvc.perform(post("/api/users/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filter))
                        .header("Authorization", JWT_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usersList", hasSize(1)))
                .andExpect(jsonPath("$.usersList[0].email", is("user1@example.com")))
                .andExpect(jsonPath("$.usersList[0].role", is("USER")));
    }

    @Test
    @WithMockUser(username = ADMIN_EMAIL, roles = "ADMIN")
    void getByFiltersWithNoMatchingFiltersShouldReturnEmptyList() throws Exception {
        UserFilterDTO filter = new UserFilterDTO();
        filter.setEmail("user@example.com");
        filter.setName("User");
        filter.setSurname("One");
        filter.setRole("USER");
        filter.setEnabled(true);

        List<User> filteredUsers = Collections.emptyList();

        given(userService.findUsers(filter.getEmail(), filter.getName(), filter.getSurname(), filter.getRole(), filter.getEnabled())).willReturn(filteredUsers);

        mockMvc.perform(post("/api/users/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filter))
                        .header("Authorization", JWT_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usersList", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getByFiltersPostAsNonAdminShouldReturnForbidden() throws Exception {
        UserFilterDTO filter = new UserFilterDTO();
        filter.setEmail("user@example.com");
        filter.setName("User");
        filter.setSurname("One");
        filter.setRole("USER");
        filter.setEnabled(true);

        mockMvc.perform(post("/api/users/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(filter))
                        .header("Authorization", JWT_TOKEN))
                .andExpect(status().isForbidden());
    }
}
