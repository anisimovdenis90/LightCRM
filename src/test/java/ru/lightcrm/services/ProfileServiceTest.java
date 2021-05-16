package ru.lightcrm.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.lightcrm.entities.Department;
import ru.lightcrm.entities.Profile;
import ru.lightcrm.entities.StaffUnit;
import ru.lightcrm.entities.User;
import ru.lightcrm.entities.dtos.ProfileDto;
import ru.lightcrm.entities.dtos.ProfileFullDto;
import ru.lightcrm.repositories.ProfileRepository;
import ru.lightcrm.services.interfaces.ProfileService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class ProfileServiceTest {

    @Autowired
    private ProfileService profileService;

    @MockBean
    private ProfileRepository profileRepository;

    private static List<Profile> testProfileList;
    private static Profile testProfile;

    @BeforeAll
    public static void prepareTestData() {
        testProfile = new Profile();
        User testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("TEST_USER");
        testUser.setPriorities(Collections.emptySet());
        StaffUnit testStaff = new StaffUnit();
        testStaff.setId(1L);
        testStaff.setName("TEST_STAFF");
        testStaff.setRoles(Collections.EMPTY_SET);
        Department testDepartment = new Department();
        testDepartment.setId(1L);
        testDepartment.setName("TEST_DEPARTMENT");
        testDepartment.setLeader(testProfile);
        testProfile.setId(1L);
        testProfile.setFirstname("Тест");
        testProfile.setLastname("Тестов");
        testProfile.setMiddlename("Тестович");
        testProfile.setPhone("89997776655");
        testProfile.setEmail("test@email.com");
        testProfile.setSex("M");
        testProfile.setUser(testUser);
        testProfile.setStaffUnit(testStaff);
        testProfile.setManagedDepartments(Collections.singletonList(testDepartment));
        testProfile.setDepartments(Collections.singletonList(testDepartment));
        testProfileList = List.of(testProfile);
    }

    @Test
    public void findByIdTest() {
        Mockito.doReturn(Optional.of(testProfile))
                .when(profileRepository)
                .findById(testProfile.getId());

        ProfileDto profileDto = profileService.findDtoById(testProfile.getId());

        Assertions.assertNotNull(profileDto);
        Assertions.assertEquals(testProfile.getFirstname(), profileDto.getFirstname());
        Assertions.assertEquals(testProfile.getLastname(), profileDto.getLastname());
        Assertions.assertEquals(testProfile.getMiddlename(), profileDto.getMiddlename());
        Mockito.verify(profileRepository, Mockito.times(1)).findById(testProfile.getId());
    }

    @Test
    public void findFullByIdTest() {
        Mockito.doReturn(Optional.of(testProfile))
                .when(profileRepository)
                .findById(testProfile.getId());

        ProfileFullDto profileFullDto = profileService.findFullDtoById(testProfile.getId());

        Assertions.assertNotNull(profileFullDto);
        Assertions.assertEquals(testProfile.getUser().getLogin(), profileFullDto.getUserLogin());
        Assertions.assertEquals(testProfile.getDepartments().get(0).getName(), profileFullDto.getManagedDepartments().get(0).getName());
        Mockito.verify(profileRepository, Mockito.times(1)).findById(testProfile.getId());
    }

    @Test
    public void findAllTest() {
        Mockito.doReturn(testProfileList)
                .when(profileRepository)
                .findAll();

        List<ProfileDto> profileDtos = profileService.findDtoAll();

        Assertions.assertNotNull(profileDtos);
        Assertions.assertEquals(testProfileList.size(), profileDtos.size());
        Assertions.assertEquals(testProfileList.get(0).getFirstname(), profileDtos.get(0).getFirstname());
        Assertions.assertEquals(testProfileList.get(0).getLastname(), profileDtos.get(0).getLastname());
        Assertions.assertEquals(testProfileList.get(0).getMiddlename(), profileDtos.get(0).getMiddlename());
        Mockito.verify(profileRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void findFullAllTest() {
        Mockito.doReturn(testProfileList)
                .when(profileRepository)
                .findAll();

        List<ProfileFullDto> profileFullDtos = profileService.findFullDtoAll();

        Assertions.assertNotNull(profileFullDtos);
        Assertions.assertEquals(testProfileList.size(), profileFullDtos.size());
        Assertions.assertEquals(testProfileList.get(0).getUser().getLogin(), profileFullDtos.get(0).getUserLogin());
        Assertions.assertEquals(testProfileList.get(0).getDepartments().get(0).getName(), profileFullDtos.get(0).getManagedDepartments().get(0).getName());
        Mockito.verify(profileRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void saveProfileTest() {
        Profile profile = new Profile();
        profileService.saveProfile(profile);
        Mockito.verify(profileRepository, Mockito.times(1)).save(profile);
    }
}
