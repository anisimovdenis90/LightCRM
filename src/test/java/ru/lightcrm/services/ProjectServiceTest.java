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
import ru.lightcrm.entities.User;
import ru.lightcrm.entities.dtos.ProjectDto;
import ru.lightcrm.repositories.ProjectRepository;
import ru.lightcrm.services.filters.ProjectFilter;
import ru.lightcrm.services.interfaces.ProjectService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


@SpringBootTest
@ActiveProfiles("test")
public class ProjectServiceTest {
    @Autowired
    private ProjectService projectService;

    @MockBean
    private ProjectRepository projectRepository;

    private static final String PROJECT_NAME = "Проект";
    private static final String PROJECT_DESCRIPTION = "Описание проекта";
    private static final int PROJECT_COUNT = 3;

    @BeforeEach
    public void init() {
        Profile manager = new Profile();
        manager.setId(1L);
        User managerUser = new User();
        managerUser.setId(1L);
        manager.setUser(managerUser);
        Project p1 = new Project(1L, PROJECT_NAME + " 1", PROJECT_DESCRIPTION + " 1", manager);
        Project p2 = new Project(2L, PROJECT_NAME + " 2", PROJECT_DESCRIPTION + " 2", manager);
        Project p3 = new Project(3L, PROJECT_NAME + " 3", PROJECT_DESCRIPTION + " 3", manager);
        List<Project> projects = new ArrayList<>();
        projects.add(p1);
        projects.add(p2);
        projects.add(p3);

        Mockito.doReturn(projects).when(projectRepository).findAll(new ProjectFilter(null).getSpecification());
        Mockito.doReturn(Optional.of(projects.get(0))).when(projectRepository).findById(p1.getId());
        Mockito.doReturn(Optional.of(projects.get(1))).when(projectRepository).findById(p2.getId());
        Mockito.doReturn(Optional.of(projects.get(2))).when(projectRepository).findById(p3.getId());
        Mockito.doReturn(Optional.of(projects.get(0))).when(projectRepository).findOneByName(p1.getName());
        Mockito.doReturn(projects).when(projectRepository).findByManagerId(p1.getManager().getId());
        Mockito.doNothing().when(projectRepository).deleteById(p3.getId());
        Mockito.doAnswer(invocation -> {
            projects.remove((int)(p3.getId() - 1));
            return null;
        }).when(projectRepository).deleteById(p3.getId());
    }

    @Test
    public void findAll() {
        List<ProjectDto> projectDTOList = projectService.findDtoAll(new HashMap<>());
        Assertions.assertNotNull(projectDTOList);
        Assertions.assertEquals(PROJECT_COUNT, projectDTOList.size());
        Assertions.assertEquals(PROJECT_NAME + " 1", projectDTOList.get(0).getName());
        Assertions.assertEquals(1, projectDTOList.get(PROJECT_COUNT - 1).getManager().getId().intValue());
    }

    @Test
    public void findById() {
        Long id = 1L;
        ProjectDto projectDTO = projectService.findDtoById(id);
        Assertions.assertNotNull(projectDTO);
        Assertions.assertEquals(PROJECT_NAME + " " + id, projectDTO.getName());
        Assertions.assertEquals(PROJECT_DESCRIPTION + " " + id, projectDTO.getDescription());
        Assertions.assertEquals(1L, projectDTO.getManager().getId().intValue());
    }

    @Test
    public void findOneByName() {
        Long id = 1L;
        final String name = PROJECT_NAME + " " + id;

        ProjectDto projectDTO = projectService.findDtoByName(name);
        Assertions.assertNotNull(projectDTO);
        Assertions.assertEquals(id, projectDTO.getId());
        Assertions.assertEquals(PROJECT_DESCRIPTION + " " + id, projectDTO.getDescription());
        Assertions.assertEquals(1, projectDTO.getManager().getId().intValue());
    }

    @Test
    public void findByManagerId() {
        Long id = 1L;

        List<ProjectDto> projectDTOList = projectService.findDtoByManagerId(id);
        Assertions.assertNotNull(projectDTOList);
        Assertions.assertEquals(PROJECT_COUNT, projectDTOList.size());
        for(int i = 0; i < PROJECT_COUNT; i++)
            Assertions.assertEquals(id, projectDTOList.get(i).getManager().getId().intValue());
    }

    @Test
    public void deleteByIdTest() {
        Long id = 3L;

        Assertions.assertEquals(PROJECT_COUNT, projectService.findDtoAll(new HashMap<>()).size());
        projectService.deleteById(id);
        Assertions.assertEquals(PROJECT_COUNT - 1, projectService.findDtoAll(null).size());
    }

    @Test
    public void saveOrUpdateTest() {
        Profile manager = new Profile();
        manager.setId(1L);
        User managerUser = new User();
        managerUser.setId(1L);
        manager.setUser(managerUser);
        Project projectToUpdate = new Project(null, PROJECT_NAME + " NEW", PROJECT_DESCRIPTION + " NEW", manager);

        projectRepository.save(projectToUpdate);
        Mockito.verify(projectRepository, Mockito.times(1)).save(projectToUpdate);
    }
}
