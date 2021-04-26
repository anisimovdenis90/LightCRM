package ru.lightcrm.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.lightcrm.entities.*;
import ru.lightcrm.entities.dtos.ProfileDto;
import ru.lightcrm.entities.dtos.ProfileFullDto;
import ru.lightcrm.entities.dtos.ProfileMiniDto;
import ru.lightcrm.entities.dtos.SystemUserDto;
import ru.lightcrm.exceptions.ResourceNotFoundException;
import ru.lightcrm.repositories.ProfileRepository;
import ru.lightcrm.services.interfaces.DepartmentService;
import ru.lightcrm.services.interfaces.ProfileService;
import ru.lightcrm.services.interfaces.StaffUnitService;
import ru.lightcrm.services.interfaces.UserService;

import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final UserService userService;
    private final DepartmentService departmentService;
    private final PasswordEncoder passwordEncoder;
    private final StaffUnitService staffUnitService;

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
        return profileRepository.findAll().stream().map(ProfileDto::new).collect(Collectors.toList());
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

        userService.saveUser(newUser);
        saveProfile(newProfile);

        log.info("Пользователь с логином: {} успешно зарегистрирован", systemUserDto.getLogin());
    }

    @Override
    public ProfileDto findByLogin(String login) {
        return new ProfileDto(profileRepository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Профиль с логином %s отсутствует", login))));
    }
}
