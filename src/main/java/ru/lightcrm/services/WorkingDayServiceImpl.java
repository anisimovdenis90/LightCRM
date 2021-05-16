package ru.lightcrm.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.lightcrm.entities.Profile;
import ru.lightcrm.entities.WorkingDay;
import ru.lightcrm.entities.dtos.WorkingDayCreationDto;
import ru.lightcrm.entities.dtos.WorkingDayDto;
import ru.lightcrm.exceptions.ResourceNotFoundException;
import ru.lightcrm.repositories.WorkingDayRepository;
import ru.lightcrm.services.interfaces.ProfileService;
import ru.lightcrm.services.interfaces.WorkingDayService;

import javax.validation.ValidationException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkingDayServiceImpl implements WorkingDayService {

    private final WorkingDayRepository workingDayRepository;
    private ProfileService profileService;

    @Autowired
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public WorkingDay findById(Long id) {
        return workingDayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Отсутствует рабочий день с id: %d", id)));
    }

    @Override
    public List<WorkingDayDto> findByDate(String date) {
        return workingDayRepository.findByDate(date).stream().map(WorkingDayDto::new).collect(Collectors.toList());
    }

    @Override
    public WorkingDayDto findByUserLoginAndDate(String login, String date) {
        WorkingDay workingDay = workingDayRepository.findByUserLoginAndDate(login, date)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Отсутствует рабочий день за дату: %s для пользователя с логином: %s", date, login)));
        return new WorkingDayDto(workingDay);
    }

    @Override
    public WorkingDayDto findByProfileIdAndDate(Long profileId, String date) {
        WorkingDay workingDay = workingDayRepository.findByProfileIdAndDate(profileId, date)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Отсутствует рабочий день за дату: %s для пользователя с id: %s", date, profileId)));
        return new WorkingDayDto(workingDay);
    }

    @Override
    public void createNewWorkingDate(WorkingDayCreationDto workingDayCreationDto, BindingResult bindingResult, boolean needExistCheck) {
        checkBindingResult(bindingResult);

        String workingDayStartDate = workingDayCreationDto.getStartTimeStamp().format(DateTimeFormatter.ISO_LOCAL_DATE);
        Optional<WorkingDay> wdFromDbOptional = workingDayRepository.findByProfileIdAndDate(workingDayCreationDto.getProfileId(), workingDayStartDate);
        if (wdFromDbOptional.isEmpty()) {
            Profile profile = profileService.findById(workingDayCreationDto.getProfileId());
            WorkingDay newWorkingDay = WorkingDay.createNewWorkingDay(workingDayCreationDto, profile);
            workingDayRepository.save(newWorkingDay);
        } else if (!needExistCheck) {
            WorkingDay wdFromDb = wdFromDbOptional.get();
            wdFromDb.setEndTimeStamp(workingDayCreationDto.getStartTimeStamp());
            workingDayRepository.save(wdFromDb);
        } else {
            throw new ValidationException(String.format("Рабочий с датой: %s для пользователя с id: %d уже начат. Нет прав на редактирование", workingDayStartDate, workingDayCreationDto.getProfileId()));
        }
    }

    @Override
    public void updateWorkingDay(WorkingDayDto workingDayDto, BindingResult bindingResult, boolean needExistCheck) {
        checkBindingResult(bindingResult);

        String workingDayStartDate = workingDayDto.getStartTimeStamp().format(DateTimeFormatter.ISO_LOCAL_DATE);
        Optional<WorkingDay> wdFromDbOptional = workingDayRepository.findByProfileIdAndDate(workingDayDto.getProfileId(), workingDayStartDate);
        if (wdFromDbOptional.isEmpty()) {
            throw new ValidationException(String.format("Отсутствует рабочий с датой: %s для пользователя с id: %d", workingDayStartDate, workingDayDto.getProfileId()));
        } else {
            WorkingDay workingDayFromDb = wdFromDbOptional.get();
            if (needExistCheck && workingDayFromDb.getEndTimeStamp() != null) {
                throw new ValidationException(String.format("Рабочий день с датой: %s для пользователя с id: %d уже завершен. Нет прав на редактирование", workingDayStartDate, workingDayDto.getProfileId()));
            } else if (!needExistCheck) {
                workingDayFromDb.setStartTimeStamp(workingDayDto.getStartTimeStamp());
            }
            workingDayFromDb.setEndTimeStamp(workingDayDto.getEndTimeStamp());
            workingDayFromDb.setReport(workingDayDto.getReport());
            workingDayRepository.save(workingDayFromDb);
        }
    }

    private void checkBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(System.lineSeparator()));
            throw new ValidationException(message);
        }
    }
}
