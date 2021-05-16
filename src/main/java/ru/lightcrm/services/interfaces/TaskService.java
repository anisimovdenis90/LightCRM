package ru.lightcrm.services.interfaces;

import org.springframework.lang.Nullable;
import ru.lightcrm.entities.Task;
import ru.lightcrm.entities.dtos.TaskDto;

import java.util.List;
import java.util.Map;

public interface TaskService {
    List<TaskDto> findDtoAll(Map<String, String> params, @Nullable List<Long> taskStatesId);

    TaskDto findDtoById(Long id);

    Task findById(Long id);

    TaskDto findDtoByTitle(String title);

    List<TaskDto> findDtoByProducerId(Long id);

    List<TaskDto> findDtoByProducerIdAndTaskStateId(Long producerId, Long taskStateId);

    List<TaskDto> findDtoByResponsibleId(Long id);

    List<TaskDto> findDtoByResponsibleIdAndTaskStateId(Long responsibleId, Long taskStateId);

    List<TaskDto> findDtoByProjectId(Long id);

    Integer countByCompanyId(Long id);

    TaskDto saveOrUpdateFromDto(TaskDto taskDto);

    void deleteById(Long id);
}
