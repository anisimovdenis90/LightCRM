package ru.lightcrm.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import ru.lightcrm.entities.*;
import ru.lightcrm.entities.dtos.UserDto;
import ru.lightcrm.repositories.ProfileRepository;
import ru.lightcrm.repositories.UserRepository;
import ru.lightcrm.services.interfaces.UserService;

import java.util.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository usersRepository;
    @MockBean
    private ProfileRepository profileRepository;

    @Test
    public void getByUsernameTest() {
        Mockito.doReturn(generateMockUser()).when(usersRepository)
                .findByLogin("user");

        UserDto user = userService.getDtoByUsername("user");
        Assertions.assertNotNull(user);
        Assertions.assertTrue(user.isEnabled());
        Assertions.assertEquals(1, user.getPriorities().size());
        Assertions.assertEquals("100", user.getPassword());
    }

    @Test
    public void loadUserByUsernameTest() {
        Mockito.doReturn(generateMockUser()).when(usersRepository)
                .findByLogin("user");

        Mockito.doReturn(generateMockProfile()).when(profileRepository)
                .findByLogin("user");

        UserDetails userDetails = userService.loadUserByUsername("user");
        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals("user", userDetails.getUsername());
        Assertions.assertEquals(1, userDetails.getAuthorities().size());
    }

    private Optional<User> generateMockUser() {
        User user = new User();
        Priority priority = new Priority();
        priority.setId(1L);
        priority.setName("TEST");
        priority.setVisibleName("Тест");
        Set<Priority> priorities = new HashSet<>(1);
        priorities.add(priority);
        user.setId(1L);
        user.setLogin("user");
        user.setPassword("100");
        user.setEnabled(true);
        user.setPriorities(priorities);
        return Optional.of(user);
    }

    private Optional<Profile> generateMockProfile() {
        Set<Priority> priorities = new HashSet<>(1);
        Set<Role> roles = new HashSet<>(1);
        Priority priority = new Priority();
        priority.setId(1L);
        priority.setName("TEST");
        priority.setVisibleName("Тест");
        priorities.add(priority);
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_TEST");
        role.setVisibleName("ТЕСТ");
        role.setPriorities(priorities);
        roles.add(role);
        StaffUnit staffUnit = new StaffUnit();
        staffUnit.setId(1L);
        staffUnit.setName("TEST");
        staffUnit.setRoles(roles);
        Profile profile = new Profile();
        profile.setId(1L);
        profile.setStaffUnit(staffUnit);
        return Optional.of(profile);
    }
}
