package ru.lightcrm.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.lightcrm.controllers.interfaces.ProjectController;
import ru.lightcrm.entities.dtos.ProjectDto;
import ru.lightcrm.services.interfaces.ProjectService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProjectControllerImpl implements ProjectController {

    private final ProjectService projectService;

    @Override
    public List<ProjectDto> getAllProjects(Map<String, String> params) {
        return projectService.findDtoAll(params);
    }

    @Override
    public ProjectDto getProjectById(Long id) {
        return projectService.findDtoById(id);
    }

    @Override
    public ProjectDto saveProject(ProjectDto projectDTO) {
        projectDTO.setId(null);
        return projectService.saveOrUpdateFromDto(projectDTO);
    }

    @Override
    public ProjectDto updateProject(ProjectDto projectDTO) {
        return projectService.saveOrUpdateFromDto(projectDTO);
    }

    @Override
    public void deleteProjectById(Long id) {
        projectService.deleteById(id);
    }

}
