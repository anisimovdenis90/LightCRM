package ru.lightcrm.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.lightcrm.entities.Profile;
import ru.lightcrm.entities.Project;
import ru.lightcrm.entities.Task;
import ru.lightcrm.entities.TaskState;
import ru.lightcrm.entities.dtos.TaskDto;
import ru.lightcrm.repositories.TaskRepository;
import ru.lightcrm.services.filters.TaskFilter;
import ru.lightcrm.services.interfaces.TaskService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class TaskServiceTest {
    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskRepository taskRepository;

    private static final String TASK_NAME = "Задача";
    private static final String TASK_DESCRIPTION = "Описание задачи";
    private static final int TASK_COUNT = 3;

    @BeforeEach
    private void init() {
        Profile producer = new Profile();
        producer.setId(1L);

        Profile responsible = new Profile();
        responsible.setId(2L);

        Project project = new Project();
        project.setId(3L);

        TaskState taskState = new TaskState();
        taskState.setId(1L);

        List<Task> tasks = new ArrayList<>();
        Task t1 = new Task(1L, TASK_NAME + " " + 1, TASK_DESCRIPTION + " " + 1, producer, responsible, taskState, project);
        Task t2 = new Task(2L, TASK_NAME + " " + 2, TASK_DESCRIPTION + " " + 2, producer, responsible, taskState, project);
        Task t3 = new Task(3L, TASK_NAME + " " + 3, TASK_DESCRIPTION + " " + 3, producer, responsible, taskState, project);
        tasks.add(t1);
        tasks.add(t2);
        tasks.add(t3);

        Mockito.doReturn(tasks).when(taskRepository).findAll(new TaskFilter(new HashMap<>(), null).getSpec());
        Mockito.doReturn(Optional.of(t1)).when(taskRepository).findById(t1.getId());
        Mockito.doReturn(Optional.of(t1)).when(taskRepository).findOneByTitle(t1.getTitle());
        Mockito.doReturn(tasks).when(taskRepository).findByProducerId(producer.getId());
        Mockito.doReturn(tasks).when(taskRepository).findByProjectId(project.getId());
        Mockito.doReturn(tasks).when(taskRepository).findByResponsibleId(responsible.getId());
        Mockito.doReturn(tasks).when(taskRepository).findByProducerIdAndTaskStateId(producer.getId(), taskState.getId());
        Mockito.doReturn(tasks).when(taskRepository).findByResponsibleIdAndTaskStateId(responsible.getId(), taskState.getId());
        Mockito.doAnswer(invocation -> {
            tasks.remove((int) (t3.getId() - 1));
            return null;
        }).when(taskRepository).deleteById(t3.getId());
    }

    @Test
    public void findAllTest() {
        List<TaskDto> taskDTOList = taskService.findDtoAll(new HashMap<>(), null);
        Assertions.assertNotNull(taskDTOList);
        Assertions.assertEquals(TASK_COUNT, taskDTOList.size());
        Assertions.assertEquals(TASK_NAME + " 1", taskDTOList.get(0).getTitle());
        Assertions.assertEquals(1, taskDTOList.get(TASK_COUNT - 1).getProducerId());
        Assertions.assertEquals(2, taskDTOList.get(TASK_COUNT - 1).getResponsibleId());
    }

    @Test
    public void findByIdTest() {
        Long id = 1L;

        TaskDto taskDTO = taskService.findDtoById(id);
        Assertions.assertNotNull(taskDTO);
        Assertions.assertEquals(TASK_NAME + " " + id, taskDTO.getTitle());
        Assertions.assertEquals(TASK_DESCRIPTION + " " + id, taskDTO.getDescription());
        Assertions.assertEquals(1L, taskDTO.getProducerId());
        Assertions.assertEquals(2L, taskDTO.getResponsibleId());
    }

    @Test
    public void findOneByTitleTest() {
        Long id = 1L;
        final String name = TASK_NAME + " " + id;

        TaskDto taskDTO = taskService.findDtoByTitle(name);
        Assertions.assertNotNull(taskDTO);
        Assertions.assertEquals(id, taskDTO.getId());
        Assertions.assertEquals(TASK_DESCRIPTION + " " + id, taskDTO.getDescription());
        Assertions.assertEquals(1, taskDTO.getTaskStateId());
    }

    @Test
    public void findByProducerIdTest() {
        Long id = 1L;

        List<TaskDto> taskDTOList = taskService.findDtoByProducerId(id);
        Assertions.assertNotNull(taskDTOList);
        Assertions.assertEquals(TASK_COUNT, taskDTOList.size());
        for(int i = 0; i < TASK_COUNT; i++)
            Assertions.assertEquals(id, taskDTOList.get(i).getProducerId());
    }

    @Test
    public void findByProducerIdAndTaskStateIdTest() {
        Long producerId = 1L, taskStateId = 1L;

        List<TaskDto> taskDTOList = taskService.findDtoByProducerIdAndTaskStateId(producerId, taskStateId);
        Assertions.assertNotNull(taskDTOList);
        Assertions.assertEquals(TASK_COUNT, taskDTOList.size());
        for(int i = 0; i < TASK_COUNT; i++) {
            Assertions.assertEquals(producerId, taskDTOList.get(i).getProducerId());
            Assertions.assertEquals(taskStateId, taskDTOList.get(i).getTaskStateId());
        }
    }

    @Test
    public void findByResponsibleIdTest() {
        Long id = 2L;

        List<TaskDto> taskDTOList = taskService.findDtoByResponsibleId(id);
        Assertions.assertNotNull(taskDTOList);
        Assertions.assertEquals(TASK_COUNT, taskDTOList.size());
        for(int i = 0; i < TASK_COUNT; i++)
            Assertions.assertEquals(id, taskDTOList.get(i).getResponsibleId());
    }

    @Test
    public void findByResponsibleIdAndTaskStateIdTest() {
        Long responsibleId = 2L, taskStateId = 1L;

        List<TaskDto> taskDTOList = taskService.findDtoByResponsibleIdAndTaskStateId(responsibleId, taskStateId);
        Assertions.assertNotNull(taskDTOList);
        Assertions.assertEquals(TASK_COUNT, taskDTOList.size());
        for(int i = 0; i < TASK_COUNT; i++) {
            Assertions.assertEquals(responsibleId, taskDTOList.get(i).getResponsibleId());
            Assertions.assertEquals(taskStateId, taskDTOList.get(i).getTaskStateId());
        }
    }

    @Test
    public void findByProjectIdTest() {
        Long id = 3L;

        List<TaskDto> taskDTOList = taskService.findDtoByProjectId(id);
        Assertions.assertNotNull(taskDTOList);
        Assertions.assertEquals(TASK_COUNT, taskDTOList.size());
        for(int i = 0; i < TASK_COUNT; i++)
            Assertions.assertEquals(id, taskDTOList.get(i).getProjectId());
    }

    @Test
    public void deleteTest() {
        Long id = 3L;

        Assertions.assertEquals(TASK_COUNT, taskService.findDtoAll(new HashMap<>(), null).size());
        taskService.deleteById(id);
        Assertions.assertEquals(TASK_COUNT - 1, taskService.findDtoAll(new HashMap<>(), null).size());
    }

    @Test
    public void saveOrUpdateTest() {
        Profile producer = new Profile();
        producer.setId(1L);

        Profile responsible = new Profile();
        responsible.setId(2L);

        Project project = new Project();
        project.setId(3L);

        TaskState taskState = new TaskState();
        taskState.setId(1L);

        Task newTask = new Task(4L, TASK_NAME + " " + 4, TASK_DESCRIPTION + " " + 4, producer, responsible, taskState, project);

        taskRepository.save(newTask);
        Mockito.verify(taskRepository, Mockito.times(1)).save(newTask);
    }
}
