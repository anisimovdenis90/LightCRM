package ru.lightcrm.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.lightcrm.entities.Comment;
import ru.lightcrm.entities.Profile;
import ru.lightcrm.entities.Project;
import ru.lightcrm.entities.Task;
import ru.lightcrm.entities.dtos.TaskDto;
import ru.lightcrm.exceptions.ResourceNotFoundException;
import ru.lightcrm.repositories.TaskRepository;
import ru.lightcrm.services.filters.TaskFilter;
import ru.lightcrm.services.interfaces.ProfileService;
import ru.lightcrm.services.interfaces.ProjectService;
import ru.lightcrm.services.interfaces.TaskService;
import ru.lightcrm.services.interfaces.TaskStateService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private ProfileService profileService;
    private TaskStateService taskStateService;
    private ProjectService projectService;
    private CommentServiceImpl commentService;

    @Autowired
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Autowired
    public void setTaskStateService(TaskStateService taskStateService) {
        this.taskStateService = taskStateService;
    }

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Autowired
    public void setCommentService(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    @Override
    public List<TaskDto> findDtoAll(Map<String, String> params, @Nullable List<Long> taskStatesId) {
        TaskFilter taskFilter = new TaskFilter(params, taskStatesId);
        return taskRepository.findAll(taskFilter.getSpec()).stream().map(TaskDto::new).collect(Collectors.toList());
    }

    @Override
    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Задача с id = %s не найден", id)));
    }

    @Override
    public TaskDto findDtoById(Long id) {
        return new TaskDto(findById(id));
    }

    @Override
    public TaskDto findDtoByTitle(String title) {
        return new TaskDto(taskRepository.findOneByTitle(title)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Задача с наименованием = \"%s\" не найдена", title))));
    }

    @Override
    public List<TaskDto> findDtoByProducerId(Long id) {
        return taskRepository.findByProducerId(id).stream().map(TaskDto::new).collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> findDtoByProducerIdAndTaskStateId(Long producerId, Long taskStateId) {
        return taskRepository.findByProducerIdAndTaskStateId(producerId, taskStateId).stream()
                .map(TaskDto::new).collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> findDtoByResponsibleId(Long id) {
        return taskRepository.findByResponsibleId(id).stream().map(TaskDto::new).collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> findDtoByResponsibleIdAndTaskStateId(Long responsibleId, Long taskStateId) {
        return taskRepository.findByResponsibleIdAndTaskStateId(responsibleId, taskStateId).stream()
                .map(TaskDto::new).collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> findDtoByProjectId(Long id) {
        return taskRepository.findByProjectId(id).stream().map(TaskDto::new).collect(Collectors.toList());
    }

    @Override
    public Integer countByCompanyId(Long id) {
        return taskRepository.countAllByCompanyId(id);
    }

    @Override
    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public TaskDto saveOrUpdateFromDto(TaskDto taskDto) {
        Task task;
        if (taskDto.getId() == null) {
            task = new Task();
        } else {
            task = taskRepository.findById(taskDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("Задача с id = %s не найдена", taskDto.getId())));
        }
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setProducer(profileService.findById(taskDto.getProducerId()));
        task.setResponsible(profileService.findById(taskDto.getResponsibleId()));
        task.setStartDate(taskDto.getStartDate());
        task.setEndDate(taskDto.getEndDate());
        task.setDeadline(taskDto.getDeadline());
        task.setTaskState(taskStateService.findEntityById(taskDto.getTaskStateId()));
        task.setAllowChangeDeadline(taskDto.isAllowChangeDeadline());
        task.setExpired(taskDto.isExpired());

        Project project = null;
        if (taskDto.getProjectId() != null) {
            project = projectService.findById(taskDto.getProjectId());
        }
        task.setProject(project);

        Set<Profile> coExecutors = null;
        if (taskDto.getCoExecutors() != null) {
            coExecutors = taskDto.getCoExecutors().stream()
                    .map(coEx -> profileService.findById(coEx.getId()))
                    .collect(Collectors.toSet());
        }
        task.setCoExecutors(coExecutors);

        Set<Profile> spectators = null;
        if (taskDto.getSpectators() != null) {
            spectators = taskDto.getSpectators().stream()
                    .map(s -> profileService.findById(s.getId()))
                    .collect(Collectors.toSet());
        }
        task.setSpectators(spectators);

        Set<Comment> comments = null;
        if (taskDto.getComments() != null) {
            comments = taskDto.getComments().stream()
                    .map(c -> commentService.findById(c.getId()))
                    .collect(Collectors.toSet());
        }
        task.setComments(comments);

        return new TaskDto(taskRepository.save(task));
    }
}
