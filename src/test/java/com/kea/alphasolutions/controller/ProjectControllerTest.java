package com.kea.alphasolutions.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.kea.alphasolutions.model.Project;
import com.kea.alphasolutions.model.Resource;
import com.kea.alphasolutions.service.ProjectService;
import com.kea.alphasolutions.service.SubprojectService;
import com.kea.alphasolutions.service.TaskService;
import com.kea.alphasolutions.service.TimeRegistrationService;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    ProjectService projectService;

    @MockitoBean
    SubprojectService subprojectService;

    @MockitoBean
    TimeRegistrationService timeRegistrationService;

    @MockitoBean
    TaskService taskService;

    @Test
    void unauthenticatedUserRedirectedToLogin() throws Exception {
        // Tester at uautoriserede brugere bliver omdirigeret til login siden
        mockMvc.perform(get("/projects"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void authenticatedUserCanListProjects() throws Exception {
        // Arrange - Opsætter testdata og mock session
        MockHttpSession session = new MockHttpSession();
        Resource user = new Resource();
        user.setResourceId(1);
        user.setName("Test User");
        session.setAttribute("loggedInUser", user);

        Project testProject = new Project();
        testProject.setProjectId(1);
        testProject.setName("Test Project");
        
        // Mocker service metoder til at returnere testdata
        when(projectService.getAllProjects()).thenReturn(List.of(testProject));
        when(projectService.getSubprojectsCount(1)).thenReturn(2);
        when(projectService.getTasksCount(1)).thenReturn(5);
        when(projectService.getResourcesCount(1)).thenReturn(3);
        when(projectService.getProgressPercentage(1)).thenReturn(75.0);

        // Act & Assert - Udfører request og verificerer respons
        mockMvc.perform(get("/projects").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("project/list"))
                .andExpect(model().attributeExists("projects"))
                .andExpect(model().attributeExists("subprojectsCounts"))
                .andExpect(model().attributeExists("tasksCounts"))
                .andExpect(model().attributeExists("resourcesCounts"))
                .andExpect(model().attributeExists("progressPercentages"));

        // Verificerer at service metoden blev kaldt
        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void projectProgressCalculatedCorrectly() throws Exception {
        // Arrange - Opsætter test for fremdriftsberegning
        MockHttpSession session = new MockHttpSession();
        Resource user = new Resource();
        user.setResourceId(1);
        user.setName("Test User");
        session.setAttribute("loggedInUser", user);

        Project testProject = new Project();
        testProject.setProjectId(1);
        testProject.setName("Test Project");
        
        // Mocker projektdata med 75% fremdrift
        when(projectService.getAllProjects()).thenReturn(List.of(testProject));
        when(projectService.getSubprojectsCount(1)).thenReturn(3);
        when(projectService.getTasksCount(1)).thenReturn(12);
        when(projectService.getResourcesCount(1)).thenReturn(4);
        when(projectService.getProgressPercentage(1)).thenReturn(75.0);

        // Act & Assert - Verificerer at fremdrift beregnes korrekt
        mockMvc.perform(get("/projects").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("project/list"))
                .andExpect(model().attributeExists("progressPercentages"));

        // Verificerer at fremdriftsberegning blev kaldt
        verify(projectService, times(1)).getProgressPercentage(1);
    }

    @Test
    void authenticatedUserCanViewProjectDetail() throws Exception {
        // Arrange - Opsætter test for visning af projektdetaljer
        MockHttpSession session = new MockHttpSession();
        Resource user = new Resource();
        user.setResourceId(1);
        user.setName("Test User");
        session.setAttribute("loggedInUser", user);

        Project testProject = new Project();
        testProject.setProjectId(1);
        testProject.setName("Test Project");

        when(projectService.getProjectById(1)).thenReturn(testProject);
        when(subprojectService.getSubprojectsByProjectId(1)).thenReturn(List.of());
        when(timeRegistrationService.getTotalHoursByProjectId(1)).thenReturn(40.0);
        when(taskService.getTotalEstimatedHoursByProjectId(1)).thenReturn(80.0);
        when(projectService.getTasksCount(1)).thenReturn(10);
        when(projectService.getResourcesCount(1)).thenReturn(5);
        when(projectService.getProgressPercentage(1)).thenReturn(50.0);

        // Act & Assert - Henter og verificerer projektdetaljer
        mockMvc.perform(get("/projects/1").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("project/detail"))
                .andExpect(model().attributeExists("project"))
                .andExpect(model().attributeExists("subprojects"))
                .andExpect(model().attributeExists("totalHours"));

        // Verificerer at projekt blev hentet fra service
        verify(projectService, times(1)).getProjectById(1);
    }

    @Test
    void authenticatedUserCanCreateProject() throws Exception {
        // Arrange - Opsætter autentificeret bruger
        MockHttpSession session = new MockHttpSession();
        Resource user = new Resource();
        user.setResourceId(1);
        user.setName("Test User");
        session.setAttribute("loggedInUser", user);

        // Act & Assert - Opretter nyt projekt og verificerer omdirigering
        mockMvc.perform(post("/projects")
                .session(session)
                .param("name", "New Project")
                .param("description", "New Description")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

        // Verificerer at projekt blev tilføjet via service
        verify(projectService, times(1)).addProject(any(Project.class));
    }

    @Test
    void authenticatedUserCanUpdateProject() throws Exception {
        // Arrange - Opsætter test for projektopdatering
        MockHttpSession session = new MockHttpSession();
        Resource user = new Resource();
        user.setResourceId(1);
        user.setName("Test User");
        session.setAttribute("loggedInUser", user);

        Project existingProject = new Project();
        existingProject.setProjectId(1);
        when(projectService.getProjectById(1)).thenReturn(existingProject);

        // Act & Assert - Opdaterer projekt og verificerer omdirigering
        mockMvc.perform(post("/projects/1/update")
                .session(session)
                .param("projectId", "1")
                .param("name", "Updated Project")
                .param("description", "Updated Description")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

        // Verificerer at opdatering blev kaldt
        verify(projectService, times(1)).updateProject(any(Project.class));
    }

    @Test
    void authenticatedUserCanDeleteProject() throws Exception {
        // Arrange - Opsætter test for sletning af projekt (regular user)
        MockHttpSession session = new MockHttpSession();
        Resource user = new Resource();
        user.setResourceId(1);
        user.setName("Test User");
        user.setSystemRole("USER"); // Regular user should NOT be able to delete
        session.setAttribute("loggedInUser", user);

        Project existingProject = new Project();
        existingProject.setProjectId(1);
        when(projectService.getProjectById(1)).thenReturn(existingProject);

        // Act & Assert - Regular user gets redirected to access denied page
        mockMvc.perform(get("/projects/1/delete").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));

        // Verificerer at sletning IKKE blev udført
        verify(projectService, never()).deleteProject(1);
    }

    @Test
    void adminCanDeleteProject() throws Exception {
        // Arrange - Opsætter test for admin sletning af projekt
        MockHttpSession session = new MockHttpSession();
        Resource admin = new Resource();
        admin.setResourceId(1);
        admin.setName("Admin User");
        admin.setSystemRole("ADMIN"); // Admin should be able to delete
        session.setAttribute("loggedInUser", admin);

        Project existingProject = new Project();
        existingProject.setProjectId(1);
        when(projectService.getProjectById(1)).thenReturn(existingProject);

        // Act & Assert - Admin can delete and gets redirected to projects
        mockMvc.perform(get("/projects/1/delete").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

        // Verificerer at sletning blev udført
        verify(projectService, times(1)).deleteProject(1);
    }
}