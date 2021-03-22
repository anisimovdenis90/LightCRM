package ru.lightcrm.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.lightcrm.entities.dtos.ProfileDto;
import ru.lightcrm.entities.dtos.ProfileFullDto;
import ru.lightcrm.exceptions.ResourceNotFoundException;
import ru.lightcrm.repositories.ProfileRepository;
import ru.lightcrm.services.interfaces.ProfileService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;

    @Override
    public ProfileDto findById(Long id) {
        return new ProfileDto(profileRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Профиль с id %s отсутствует", id))));
    }

    @Override
    public List<ProfileDto> findAll() {
        return profileRepository.findAll().stream().map(ProfileDto::new).collect(Collectors.toList());
    }

    @Override
    public ProfileFullDto findFullById(Long id) {
        return new ProfileFullDto(profileRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Профиль с id %s отсутствует", id))));
    }

    @Override
    public List<ProfileFullDto> findFullAll() {
        return profileRepository.findAll().stream().map(ProfileFullDto::new).collect(Collectors.toList());
    }

    @Override
    public ProfileDto findByLogin(String login) {
        return new ProfileDto(profileRepository.findByLogin(login).orElseThrow(() -> new ResourceNotFoundException(String.format("Профиль с логином %s отсутствует", login))));
    }
}
