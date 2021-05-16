package ru.lightcrm.services.interfaces;

import org.springframework.validation.BindingResult;
import ru.lightcrm.entities.Profile;
import ru.lightcrm.entities.dtos.ProfileDto;
import ru.lightcrm.entities.dtos.ProfileFullDto;
import ru.lightcrm.entities.dtos.ProfileMiniDto;
import ru.lightcrm.entities.dtos.SystemUserDto;

import java.util.List;

public interface ProfileService {

    Profile findById(Long id);
    Profile findByUserLogin(String login);

    ProfileDto findDtoById(Long id);
    List<ProfileDto> findDtoAll();

    ProfileFullDto findFullDtoById(Long id);
    ProfileFullDto findFullDtoByUserId(Long userId);
    ProfileFullDto findFullDtoByUserLogin(String name);
    List<ProfileFullDto> findFullDtoAll();

    ProfileMiniDto findMiniDtoById(Long id);
    ProfileMiniDto findMiniDtoByUserLogin(String name);
    List<ProfileMiniDto> findMiniDtoAll();

    ProfileDto findDtoByLogin(String login);

    void saveProfile(Profile profile);

    void saveNewUser(SystemUserDto systemUserDto, BindingResult bindingResult);

    <P extends ProfileMiniDto> void updateProfile(P dto, BindingResult bindingResult);

}
