package ru.lightcrm.controllers.interfaces;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.lightcrm.entities.dtos.TaskDto;
import ru.lightcrm.services.interfaces.TaskService;


import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TaskService taskService;

    private static List<TaskDto> testListTaskDto;
    private static TaskDto testTaskDto;

    private static TaskDto testNewTaskDto;
    private static TaskDto testReturnedNewTaskDto;

    private static TaskDto testModifyTaskDto;

    @BeforeAll
    public static void init(){
        testTaskDto = new TaskDto();
        testTaskDto.setId(1L);
        testTaskDto.setTitle("Task test");
        testTaskDto.setDescription("Description of task");
        testTaskDto.setProducerId(1L);
        testTaskDto.setResponsibleId(1L);
        testTaskDto.setProjectId(1L);

        // save test
        testNewTaskDto = new TaskDto();
        testNewTaskDto.setTitle("Task test NEW");
        testNewTaskDto.setDescription("Description of task NEW");
        testNewTaskDto.setProducerId(1L);
        testNewTaskDto.setResponsibleId(1L);
        testNewTaskDto.setProjectId(1L);
        testNewTaskDto.setTaskStateId(1L);

        testReturnedNewTaskDto = new TaskDto();
        testReturnedNewTaskDto.setId(2L);
        testReturnedNewTaskDto.setTitle(testNewTaskDto.getTitle());
        testReturnedNewTaskDto.setDescription(testNewTaskDto.getDescription());
        testReturnedNewTaskDto.setProducerId(testNewTaskDto.getProducerId());
        testReturnedNewTaskDto.setResponsibleId(testNewTaskDto.getResponsibleId());
        testReturnedNewTaskDto.setProjectId(testNewTaskDto.getProjectId());
        testReturnedNewTaskDto.setTaskStateId(testNewTaskDto.getTaskStateId());

        // update test
        testModifyTaskDto = new TaskDto();
        testModifyTaskDto.setId(1L);
        testModifyTaskDto.setTitle(testTaskDto.getTitle() + " MODIFIED");
        testModifyTaskDto.setDescription(testTaskDto.getDescription() + " MODIFIED");
        testModifyTaskDto.setProducerId(2L);
        testModifyTaskDto.setResponsibleId(2L);
        testModifyTaskDto.setProjectId(2L);
        testModifyTaskDto.setTaskStateId(2L);

        testListTaskDto = List.of(testTaskDto);
    }

    @Test
    @WithMockUser(username = "Bob", authorities = "ADMIN")
    public void getAllTasksTest() throws Exception{
        given(taskService.findDtoAll(new HashMap<>(), null)).willReturn(testListTaskDto);

        mvc.perform(get("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is(testTaskDto.getTitle())))
                .andExpect(jsonPath("$[0].description", is(testTaskDto.getDescription())))
                .andExpect(jsonPath("$[0].producerId", is(testTaskDto.getProducerId().intValue())))
                .andExpect(jsonPath("$[0].responsibleId", is(testTaskDto.getResponsibleId().intValue())))
                .andExpect(jsonPath("$[0].projectId", is(testTaskDto.getProjectId().intValue())));
    }

    @Test
    @WithMockUser(username = "Bob", authorities = "ADMIN")
    public void getTaskByIdTest() throws Exception{
        given(taskService.findDtoById(1L)).willReturn(testTaskDto);

        mvc.perform(get("/api/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(testTaskDto.getTitle())))
                .andExpect(jsonPath("$.description", is(testTaskDto.getDescription())))
                .andExpect(jsonPath("$.producerId", is(testTaskDto.getProducerId().intValue())))
                .andExpect(jsonPath("$.responsibleId", is(testTaskDto.getResponsibleId().intValue())))
                .andExpect(jsonPath("$.projectId", is(testTaskDto.getProjectId().intValue())));
    }

    @Test
    @WithMockUser(username = "Bob", authorities = "ADMIN")
    public void saveTaskTest() throws Exception {
        given(taskService.saveOrUpdateFromDto(testNewTaskDto)).willReturn(testReturnedNewTaskDto);

        String json = "{ \"title\": \"" + testNewTaskDto.getTitle() + "\", " +
                "\"description\" : \"" + testNewTaskDto.getDescription() + "\", " +
                "\"producerId\" : " + testNewTaskDto.getProducerId() + "," +
                "\"responsibleId\" : " + testNewTaskDto.getResponsibleId() + "," +
                "\"projectId\" : " + testNewTaskDto.getProjectId() + "," +
                "\"taskStateId\" : " + testNewTaskDto.getTaskStateId() + " }";

        mvc.perform(post("/api/v1/tasks/").content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testReturnedNewTaskDto.getId().intValue())))
                .andExpect(jsonPath("$.title", is(testReturnedNewTaskDto.getTitle())))
                .andExpect(jsonPath("$.description", is(testReturnedNewTaskDto.getDescription())))
                .andExpect(jsonPath("$.producerId", is(testReturnedNewTaskDto.getProducerId().intValue())))
                .andExpect(jsonPath("$.responsibleId", is(testReturnedNewTaskDto.getResponsibleId().intValue())))
                .andExpect(jsonPath("$.projectId", is(testReturnedNewTaskDto.getProjectId().intValue())))
                .andExpect(jsonPath("$.taskStateId", is(testReturnedNewTaskDto.getTaskStateId().intValue())));
    }

    @Test
    @WithMockUser(username = "Bob", authorities = "ADMIN")
    public void updateTaskTest() throws Exception {
        given(taskService.saveOrUpdateFromDto(testModifyTaskDto)).willReturn(testModifyTaskDto);

        String json = "{ \"id\" : " + testModifyTaskDto.getId() + ", " +
                "\"title\": \"" + testModifyTaskDto.getTitle() + "\", " +
                "\"description\" : \"" + testModifyTaskDto.getDescription() + "\", " +
                "\"producerId\" : " + testModifyTaskDto.getProducerId() + "," +
                "\"responsibleId\" : " + testModifyTaskDto.getResponsibleId() + "," +
                "\"projectId\" : " + testModifyTaskDto.getProjectId() + "," +
                "\"taskStateId\" : " + testModifyTaskDto.getTaskStateId() + "}";

        mvc.perform(put("/api/v1/tasks/").content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testModifyTaskDto.getId().intValue())))
                .andExpect(jsonPath("$.title", is(testModifyTaskDto.getTitle())))
                .andExpect(jsonPath("$.description", is(testModifyTaskDto.getDescription())))
                .andExpect(jsonPath("$.producerId", is(testModifyTaskDto.getProducerId().intValue())))
                .andExpect(jsonPath("$.responsibleId", is(testModifyTaskDto.getResponsibleId().intValue())))
                .andExpect(jsonPath("$.projectId", is(testModifyTaskDto.getProjectId().intValue())))
                .andExpect(jsonPath("$.taskStateId", is(testModifyTaskDto.getTaskStateId().intValue())));
    }

    @Test
    @WithMockUser(username = "Bob", authorities = "ADMIN")
    public void deleteTest() throws Exception {
        mvc.perform(delete("/api/v1/tasks/" + testTaskDto.getId()))
                .andExpect(status().isOk());

        mvc.perform(get("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
