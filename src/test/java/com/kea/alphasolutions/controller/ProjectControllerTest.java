package com.kea.alphasolutions.controller;

import com.kea.alphasolutions.model.Project;
import com.kea.alphasolutions.service.ProjectService;
import com.kea.alphasolutions.service.SubprojectService;
import com.kea.alphasolutions.service.TimeRegistrationService;
import com.kea.alphasolutions.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        mockMvc.perform(get("/projects"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void authenticatedUserCanListProjects() throws Exception {
        // Arrange
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUser", "testuser");

        Project testProject = new Project();
        testProject.setProjectId(1);
        testProject.setName("Test Project");
        
        when(projectService.getAllProjects()).thenReturn(List.of(testProject));
        when(projectService.getSubprojectsCount(1)).thenReturn(2);
        when(projectService.getTasksCount(1)).thenReturn(5);
        when(projectService.getResourcesCount(1)).thenReturn(3);
        when(projectService.getProgressPercentage(1)).thenReturn(75.0);

        // Act & Assert
        mockMvc.perform(get("/projects").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("project/list"))
                .andExpect(model().attributeExists("projects"))
                .andExpect(model().attributeExists("subprojectsCounts"))
                .andExpect(model().attributeExists("tasksCounts"))
                .andExpect(model().attributeExists("resourcesCounts"))
                .andExpect(model().attributeExists("progressPercentages"));

        verify(projectService, times(1)).getAllProjects();
    }

    @Test
    void authenticatedUserCanViewProjectDetail() throws Exception {
        // Arrange
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUser", "testuser");

        Project testProject = new Project();
        testProject.setProjectId(1);
        testProject.setName("Test Project");

        when(projectService.getProjectById(1)).thenReturn(testProject);
        when(subprojectService.getSubprojectsByProjectId(1)).thenReturn(List.of());
        when(timeRegistrationService.getTotalHoursByProjectId(1)).thenReturn(40.0);

        // Act & Assert
        mockMvc.perform(get("/projects/1").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("project/detail"))
                .andExpect(model().attributeExists("project"))
                .andExpect(model().attributeExists("subprojects"))
                .andExpect(model().attributeExists("totalHours"));

        verify(projectService, times(1)).getProjectById(1);
    }

    @Test
    void authenticatedUserCanCreateProject() throws Exception {
        // Arrange
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUser", "testuser");

        // Act & Assert
        mockMvc.perform(post("/projects")
                .session(session)
                .param("name", "New Project")
                .param("description", "New Description")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

        verify(projectService, times(1)).addProject(any(Project.class));
    }

    @Test
    void authenticatedUserCanUpdateProject() throws Exception {
        // Arrange
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUser", "testuser");

        Project existingProject = new Project();
        existingProject.setProjectId(1);
        when(projectService.getProjectById(1)).thenReturn(existingProject);

        // Act & Assert
        mockMvc.perform(post("/projects/1/update")
                .session(session)
                .param("projectId", "1")
                .param("name", "Updated Project")
                .param("description", "Updated Description")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

        verify(projectService, times(1)).updateProject(any(Project.class));
    }

    @Test
    void authenticatedUserCanDeleteProject() throws Exception {
        // Arrange
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUser", "testuser");

        Project existingProject = new Project();
        existingProject.setProjectId(1);
        when(projectService.getProjectById(1)).thenReturn(existingProject);

        // Act & Assert
        mockMvc.perform(get("/projects/1/delete").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));

        verify(projectService, times(1)).deleteProject(1);
    }
}