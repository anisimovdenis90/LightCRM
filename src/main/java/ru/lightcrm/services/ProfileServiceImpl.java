package ru.lightcrm.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.lightcrm.entities.Department;
import ru.lightcrm.entities.Profile;
import ru.lightcrm.entities.StaffUnit;
import ru.lightcrm.entities.User;
import ru.lightcrm.entities.dtos.ProfileDto;
import ru.lightcrm.entities.dtos.ProfileFullDto;
import ru.lightcrm.entities.dtos.ProfileMiniDto;
import ru.lightcrm.entities.dtos.SystemUserDto;
import ru.lightcrm.exceptions.ResourceNotFoundException;
import ru.lightcrm.repositories.ProfileRepository;
import ru.lightcrm.services.interfaces.*;

import javax.validation.ValidationException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private UserService userService;
    private DepartmentService departmentService;
    private PasswordEncoder passwordEncoder;
    private StaffUnitService staffUnitService;
    private PriorityService priorityService;
    private CommentService commentService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setStaffUnitService(StaffUnitService staffUnitService) {
        this.staffUnitService = staffUnitService;
    }

    @Autowired
    public void setPriorityService(PriorityService priorityService) {
        this.priorityService = priorityService;
    }

    @Autowired
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    @Override
    public Profile findById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Профиль с id %s отсутствует", id)));
    }

    @Override
    public Profile findByUserLogin(String login) {
        return profileRepository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Профиль пользователя с логином %s отсутствует", login)));
    }

    @Override
    public ProfileDto findDtoById(Long id) {
        return new ProfileDto(findById(id));
    }

    @Override
    public List<ProfileDto> findDtoAll() {
        return profileRepository.findAll().stream().map(ProfileDto::new).sorted(Comparator.comparing(ProfileDto::getId)).collect(Collectors.toList());
    }

    @Override
    public ProfileFullDto findFullDtoById(Long id) {
        return new ProfileFullDto(findById(id));
    }

    @Override
    public ProfileFullDto findFullDtoByUserId(Long userId) {
        return new ProfileFullDto(
                profileRepository.findByUserId(userId).orElseThrow(() ->
                        new ResourceNotFoundException(String.format("Профиль с user id %d отсутствует", userId))));
    }

    @Override
    public ProfileFullDto findFullDtoByUserLogin(String login) {
        return new ProfileFullDto(findByUserLogin(login));
    }

    @Override
    public List<ProfileFullDto> findFullDtoAll() {
        return profileRepository.findAll().stream().map(ProfileFullDto::new).collect(Collectors.toList());
    }

    @Override
    public ProfileMiniDto findMiniDtoById(Long id) {
        return new ProfileMiniDto(findById(id));
    }

    @Override
    public ProfileMiniDto findMiniDtoByUserLogin(String login) {
        return new ProfileMiniDto(findByUserLogin(login));
    }

    @Override
    public List<ProfileMiniDto> findMiniDtoAll() {
        return profileRepository.findAll().stream().map(ProfileMiniDto::new).collect(Collectors.toList());
    }

    @Override
    public void saveProfile(Profile profile) {
        profileRepository.save(profile);
    }

    @Override
    @Transactional
    public void saveNewUser(SystemUserDto systemUserDto, BindingResult bindingResult) {
        log.info("Запрос на регистрацию нового пользователя");

        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(System.lineSeparator()));
            throw new ValidationException(message);
        }

        if (!systemUserDto.getPassword().equals(systemUserDto.getConfirmationPassword())) {
            String message = "Пароль и подтверждающий пароль не совпадают";
            throw new ValidationException(message);
        }

        if (userService.isPresent(systemUserDto.getLogin())) {
            String message = String.format("Пользователь с логином: %s уже существует", systemUserDto.getLogin());
            throw new ValidationException(message);
        }

        User newUser = User.createNewUser(systemUserDto.getLogin(), passwordEncoder.encode(systemUserDto.getPassword()));

        StaffUnit staffUnit = staffUnitService.findByName(systemUserDto.getStaffUnitName());

        List<Department> departments = systemUserDto.getDepartmentNames() != null
                ? systemUserDto.getDepartmentNames().stream()
                .map(departmentService::findOneByName)
                .collect(Collectors.toList())
                : null;

        Profile newProfile = Profile.createNewProfileForUserRegistration(systemUserDto, newUser, staffUnit, departments);
        saveProfile(newProfile);

        log.info("Пользователь с логином: {} успешно зарегистрирован", systemUserDto.getLogin());
    }

    @Override
    public ProfileDto findDtoByLogin(String login) {
        return new ProfileDto(findByUserLogin(login));
    }

    @Override
    @Transactional
    public <P extends ProfileMiniDto> void updateProfile(P dto, BindingResult bindingResult) {
        checkOnErrors(bindingResult);
        if (dto.getId() == null) {
            throw new ValidationException("Отсутствует id для обновление профиля");
        }
        Profile profile = findById(dto.getId());
        if (dto instanceof ProfileFullDto) {
            updateProfileFromFullDto((ProfileFullDto) dto, profile);
        } else if (dto instanceof ProfileDto) {
            updateProfileFromDto((ProfileDto) dto, profile);
        } else {
            updateProfileFromMiniDto(dto, profile);
        }
        saveProfile(profile);
    }

    private void updateProfileFromMiniDto(ProfileMiniDto profileMiniDto, Profile profile) {
        profile.setFirstname(profileMiniDto.getFirstname());
        profile.setLastname(profileMiniDto.getLastname());
        profile.setMiddlename(profileMiniDto.getMiddlename());
        profile.setEmploymentDate(profileMiniDto.getEmploymentDate());
        profile.setStaffUnit(staffUnitService.findByName(profileMiniDto.getStaffUnitName()));
        profile.setDepartments(profileMiniDto.getDepartmentNames().stream()
                .map(departmentService::findOneByName).collect(Collectors.toList()));
    }

    private void updateProfileFromDto(ProfileDto profileDto, Profile profile) {
        updateProfileFromMiniDto(profileDto, profile);
        User user = userService.getByUsername(profileDto.getUserLogin());
        user.setPriorities(profileDto.getPriorities().stream()
                .map(priorityDto -> priorityService.getById(priorityDto.getId())).collect(Collectors.toSet()));
        profile.setUser(user);
    }

    private void updateProfileFromFullDto(ProfileFullDto profileFullDto, Profile profile) {
        updateProfileFromDto(profileFullDto, profile);
        profile.setSex(profileFullDto.getSex());
        profile.setPhone(profileFullDto.getPhone());
        profile.setEmail(profileFullDto.getEmail());
        profile.setAbout(profileFullDto.getAbout());
        profile.setBirthdate(profileFullDto.getBirthdate());
        profile.setDismissalDate(profileFullDto.getDismissalDate());
        profile.setComments(profileFullDto.getComments().stream()
                .map(commentDto -> commentService.findById(commentDto.getId())).collect(Collectors.toList()));
        profile.setManagedDepartments(profileFullDto.getManagedDepartments().stream()
                .map(departmentDto -> departmentService.findById(departmentDto.getId())).collect(Collectors.toList()));
    }
}
