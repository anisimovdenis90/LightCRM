package ru.lightcrm.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.lightcrm.entities.Profile;
import ru.lightcrm.entities.Project;
import ru.lightcrm.entities.Task;
import ru.lightcrm.entities.dtos.ProjectDto;
import ru.lightcrm.exceptions.ResourceNotFoundException;
import ru.lightcrm.repositories.ProjectRepository;
import ru.lightcrm.services.filters.ProjectFilter;
import ru.lightcrm.services.interfaces.ProfileService;
import ru.lightcrm.services.interfaces.ProjectService;
import ru.lightcrm.services.interfaces.TaskService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private ProfileService profileService;
    private TaskService taskService;

    @Autowired
    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @Autowired
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public List<ProjectDto> findDtoAll(Map<String, String> params) {
        ProjectFilter projectFilter = new ProjectFilter(params);
        return projectRepository.findAll(projectFilter.getSpecification()).stream().map(ProjectDto::new).collect(Collectors.toList());
    }

    @Override
    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Проект с id = %s не найден", id)));
    }

    @Override
    public ProjectDto findDtoById(Long id) {
        return new ProjectDto(projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Проект с id = %s не найден", id))));
    }

    @Override
    public ProjectDto findDtoByName(String name) {
        return new ProjectDto(projectRepository.findOneByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Проект с наименованием = \"%s\" не найден", name))));
    }

    @Override
    public List<ProjectDto> findDtoByManagerId(Long id) {
        return projectRepository.findByManagerId(id).stream().map(ProjectDto::new).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        projectRepository.deleteById(id);
    }

    @Override
    public ProjectDto saveOrUpdateFromDto(ProjectDto projectDto) {
        Project project;
        if (projectDto.getId() == null) {
            project = new Project();
        } else {
            project = projectRepository.findById(projectDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("Проект с id = %s не найден", projectDto.getId())));
        }
        project.setName(projectDto.getName());
        project.setDescription(projectDto.getDescription());
        project.setManager(profileService.findById(projectDto.getManager().getId()));

        List<Profile> profiles = null;
        if (projectDto.getProfiles() != null) {
            profiles = projectDto.getProfiles().stream()
                    .map(profileDto -> profileService.findById(profileDto.getId()))
                    .collect(Collectors.toList());
        }
        project.setProfiles(profiles);

        List<Task> tasks = null;
        if (projectDto.getTasks() != null) {
            tasks = projectDto.getTasks().stream()
                    .map(taskDto -> taskService.findById(taskDto.getId()))
                    .collect(Collectors.toList());
        }
        project.setTasks(tasks);

        return new ProjectDto(projectRepository.save(project));
    }
}
