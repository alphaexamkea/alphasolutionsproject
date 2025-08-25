package com.kea.alphasolutions.controller;

import com.kea.alphasolutions.model.Task;
import com.kea.alphasolutions.model.TimeRegistration;
import com.kea.alphasolutions.model.Resource;
import com.kea.alphasolutions.service.TaskService;
import com.kea.alphasolutions.service.TimeRegistrationService;
import com.kea.alphasolutions.service.ResourceService;
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

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    TaskService taskService;

    @MockitoBean
    TimeRegistrationService timeRegistrationService;

    @MockitoBean
    ResourceService resourceService;

    @Test
    void unauthenticatedUserRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void authenticatedUserCanViewTaskDetail() throws Exception {
        // Arrange
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUser", "testuser");

        Task testTask = new Task();
        testTask.setTaskId(1);
        testTask.setName("Test Task");

        TimeRegistration timeReg = new TimeRegistration();
        timeReg.setResourceId(1);
        timeReg.setHours(4.0);

        Resource testResource = new Resource();
        testResource.setResourceId(1);
        testResource.setName("Test Resource");

        when(taskService.getTaskById(1)).thenReturn(testTask);
        when(timeRegistrationService.getTimeRegistrationsByTaskId(1)).thenReturn(List.of(timeReg));
        when(timeRegistrationService.getTotalHoursByTaskId(1)).thenReturn(4.0);
        when(resourceService.getResourceById(1)).thenReturn(testResource);

        // Act & Assert
        mockMvc.perform(get("/tasks/1").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("task/detail"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attributeExists("timeRegistrations"))
                .andExpect(model().attributeExists("resourceNames"))
                .andExpect(model().attributeExists("totalHours"));

        verify(taskService, times(1)).getTaskById(1);
    }

    @Test
    void authenticatedUserCanCreateTask() throws Exception {
        // Arrange
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUser", "testuser");

        // Act & Assert
        mockMvc.perform(post("/subprojects/1/tasks")
                .session(session)
                .param("name", "New Task")
                .param("description", "New Task Description")
                .param("estimatedHours", "8.0")
                .param("deadline", "2024-12-31"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/subprojects/1"));

        verify(taskService, times(1)).addTask(any(Task.class));
    }

    @Test
    void authenticatedUserCanUpdateTask() throws Exception {
        // Arrange
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loggedInUser", "testuser");

        Task existingTask = new Task();
        existingTask.setTaskId(1);
        existingTask.setSubprojectId(1);
        when(taskService.getTaskById(1)).thenReturn(existingTask);

        // Act & Assert
        mockMvc.perform(post("/tasks/1/update")
                .session(session)
                .param("taskId", "1")
                .param("subprojectId", "1")
                .param("name", "Updated Task")
                .param("description", "Updated Description")
                .param("estimatedHours", "10.0")
                .param("deadline", "2024-12-31")
                .param("status", "IN_PROGRESS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/subprojects/1"));

        verify(taskService, times(1)).updateTask(any(Task.class));
    }
}