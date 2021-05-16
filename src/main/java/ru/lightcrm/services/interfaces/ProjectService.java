package ru.lightcrm.services.interfaces;

import ru.lightcrm.entities.Project;
import ru.lightcrm.entities.dtos.ProjectDto;

import java.util.List;
import java.util.Map;

public interface ProjectService {

    List<ProjectDto> findDtoAll(Map<String, String> params);

    Project findById(Long id);

    ProjectDto findDtoById(Long id);

    ProjectDto findDtoByName(String name);

    List<ProjectDto> findDtoByManagerId(Long id);

    ProjectDto saveOrUpdateFromDto(ProjectDto projectDTO);

    void deleteById(Long id);
}
