package ru.lightcrm.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.lightcrm.configs.JwtTokenUtil;
import ru.lightcrm.entities.Priority;
import ru.lightcrm.entities.Profile;
import ru.lightcrm.entities.Role;
import ru.lightcrm.entities.User;
import ru.lightcrm.entities.dtos.UserDto;
import ru.lightcrm.exceptions.ResourceNotFoundException;
import ru.lightcrm.repositories.UserRepository;
import ru.lightcrm.services.interfaces.ProfileService;
import ru.lightcrm.services.interfaces.UserService;
import ru.lightcrm.utils.JwtRequest;
import ru.lightcrm.utils.JwtResponse;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository usersRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private ProfileService profileService;

    @Autowired
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public User getByUsername(String username) {
        return usersRepository.findByLogin(username)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Пользователь '%s' не найден", username)));
    }

    @Override
    public UserDto getDtoByUsername(String username) {
        return new UserDto(getByUsername(username));
    }

    @Override
    public void saveUser(User user) {
        usersRepository.save(user);
    }

    @Override
    public boolean isPresent(String login) {
        return usersRepository.existsByLogin(login);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getByUsername(username);
        return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), getAuthorities(user));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return getGrantedAuthorities(getPriorities(user));
    }

    private Collection<? extends GrantedAuthority> getGrantedAuthorities(Set<String> priorities) {
        return priorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private Set<String> getPriorities(User user) {
        Set<String> priorityNames = user.getPriorities().stream()
                .map(Priority::getName)
                .collect(Collectors.toSet());
        priorityNames.addAll(profileService.findByUserLogin(user.getLogin()).getStaffUnit().getRoles().stream()
                .flatMap(role -> role.getPriorities().stream())
                .map(Priority::getName)
                .collect(Collectors.toSet()));
        return priorityNames;
    }

    public JwtResponse createAuthToken(JwtRequest jwtRequest) {
        String username = jwtRequest.getUsername();

        User user = getByUsername(username);
        if (!user.isEnabled()) {
            log.warn("User with login: {} deleted", username);
            throw new ResourceNotFoundException("Account deleted");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(),
                        jwtRequest.getPassword()));
        UserDetails userDetails = loadUserByUsername(username);
        Profile profile = profileService.findByUserLogin(username);
        String token = jwtTokenUtil.generateToken(userDetails, profile.getId());
        log.info("Successfully created token for user login {}", username);
        return new JwtResponse(token);

    }
}
