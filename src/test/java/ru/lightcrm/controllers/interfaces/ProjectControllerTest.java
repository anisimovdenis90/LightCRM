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
import ru.lightcrm.entities.dtos.ProfileDto;
import ru.lightcrm.entities.dtos.ProjectDto;
import ru.lightcrm.services.interfaces.ProjectService;

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
public class ProjectControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProjectService projectService;

    private static List<ProjectDto> testListProjectDto;
    private static ProjectDto testProjectDto;
    private static ProfileDto testProfileDto;

    private static ProjectDto testNewProjectDto;
    private static ProjectDto testReturnedNewProjectDto;

    private static ProjectDto testModifyProjectDto;
    private static ProfileDto testProfileModifyDto;

    @BeforeAll
    public static void init(){
        testProjectDto = new ProjectDto();
        testProjectDto.setId(1L);
        testProjectDto.setName("Project test");
        testProjectDto.setDescription("Description of project");

        testProfileDto = new ProfileDto();
        testProfileDto.setId(1L);
        testProjectDto.setManager(testProfileDto);

        testListProjectDto = List.of(testProjectDto);

        // saveProjectTest
        testNewProjectDto = new ProjectDto();
        testNewProjectDto.setName("Project test NEW");
        testNewProjectDto.setDescription("Description of project NEW");
        testNewProjectDto.setManager(testProfileDto);

        testReturnedNewProjectDto = new ProjectDto();
        testReturnedNewProjectDto.setId(2L);
        testReturnedNewProjectDto.setName(testNewProjectDto.getName());
        testReturnedNewProjectDto.setDescription(testNewProjectDto.getDescription());
        testReturnedNewProjectDto.setManager(testNewProjectDto.getManager());

        // updateProjectTest
        testProfileModifyDto = new ProfileDto();
        testProfileModifyDto.setId(2L);

        testModifyProjectDto = new ProjectDto();
        testModifyProjectDto.setId(1L);
        testModifyProjectDto.setName(testProjectDto.getName() + " MODIFIED");
        testModifyProjectDto.setDescription(testProjectDto.getDescription() + " MODIFIED");
        testModifyProjectDto.setManager(testProfileModifyDto);
    }

    @Test
    @WithMockUser(username = "Bob", authorities = "ADMIN")
    public void getAllProjectsTest() throws Exception{
        given(projectService.findDtoAll(new HashMap<>())).willReturn(testListProjectDto);

        mvc.perform(get("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(testProjectDto.getName())))
                .andExpect(jsonPath("$[0].description", is(testProjectDto.getDescription())))
                .andExpect(jsonPath("$[0].manager.id", is(testProjectDto.getManager().getId().intValue())));

    }

    @Test
    @WithMockUser(username = "Bob", authorities = "ADMIN")
    public void getProjectByIdTest() throws Exception{
        given(projectService.findDtoById(1L)).willReturn(testProjectDto);

        mvc.perform(get("/api/v1/projects/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(testProjectDto.getName())))
                .andExpect(jsonPath("$.description", is(testProjectDto.getDescription())))
                .andExpect(jsonPath("$.manager.id", is(testProjectDto.getManager().getId().intValue())));
    }

    @Test
    @WithMockUser(username = "Bob", authorities = "ADMIN")
    public void saveProjectTest() throws Exception {
        given(projectService.saveOrUpdateFromDto(testNewProjectDto)).willReturn(testReturnedNewProjectDto);

        String json = "{ \"name\": \"" + testNewProjectDto.getName() + "\", " +
                "\"description\" : \"" + testNewProjectDto.getDescription() + "\", " +
                "\"manager\" : { \"id\" : " + testNewProjectDto.getManager().getId() + " } }";

        mvc.perform(post("/api/v1/projects/").content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(testReturnedNewProjectDto.getName())))
                .andExpect(jsonPath("$.description", is(testReturnedNewProjectDto.getDescription())))
                .andExpect(jsonPath("$.manager.id", is(testReturnedNewProjectDto.getManager().getId().intValue())));
    }

    @Test
    @WithMockUser(username = "Bob", authorities = "ADMIN")
    public void updateProjectTest() throws Exception {
        given(projectService.saveOrUpdateFromDto(testModifyProjectDto)).willReturn(testModifyProjectDto);

        String json = "{ \"id\" : " + testModifyProjectDto.getId() + ", " +
                "\"name\": \"" + testProjectDto.getName() + " MODIFIED" + "\", " +
                "\"description\" : \"" + testProjectDto.getDescription() + " MODIFIED" + "\", " +
                "\"manager\" : { \"id\" : " + testModifyProjectDto.getManager().getId() + " } }";

        mvc.perform(put("/api/v1/projects/").content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testModifyProjectDto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(testModifyProjectDto.getName())))
                .andExpect(jsonPath("$.description", is(testModifyProjectDto.getDescription())))
                .andExpect(jsonPath("$.manager.id", is(testModifyProjectDto.getManager().getId().intValue())));
    }

    @Test
    @WithMockUser(username = "Bob", authorities = "ADMIN")
    public void deleteProjectTest() throws Exception {
        mvc.perform(delete("/api/v1/projects/" + testProjectDto.getId()))
                .andExpect(status().isOk());

        mvc.perform(get("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
