package com.kea.alphasolutions.controller;

import com.kea.alphasolutions.model.TimeRegistration;
import com.kea.alphasolutions.model.Resource;
import com.kea.alphasolutions.service.TimeRegistrationService;
import com.kea.alphasolutions.service.ResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TimeRegistrationController.class)
public class TimeRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    TimeRegistrationService timeRegistrationService;

    @MockitoBean
    ResourceService resourceService;

    @Test
    void unauthenticatedUserRedirectedToLogin() throws Exception {
        // Tester at uautoriserede brugere omdirigeres til login
        mockMvc.perform(post("/tasks/1/timeregistrations"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void authenticatedUserCanCreateTimeRegistration() throws Exception {
        // Arrange - Autentificeret bruger registrerer arbejdstid
        MockHttpSession session = new MockHttpSession();
        Resource user = new Resource();
        user.setResourceId(1);
        user.setName("Test User");
        session.setAttribute("loggedInUser", user);

        Resource testResource = new Resource();
        testResource.setResourceId(1);
        testResource.setName("Test Resource");

        when(resourceService.getAllResources()).thenReturn(List.of(testResource));

        // Act & Assert - Opretter tidsregistrering og omdirigerer
        mockMvc.perform(post("/tasks/1/timeregistrations")
                .session(session)
                .param("resourceId", "1")
                .param("date", "2024-01-01")
                .param("hours", "4.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/1"));

        // Verificerer at tidsregistrering blev tilføjet
        verify(timeRegistrationService, times(1)).addTimeRegistration(any(TimeRegistration.class));
    }

    @Test
    void authenticatedUserCanUpdateTimeRegistration() throws Exception {
        // Arrange - Opdatering af eksisterende tidsregistrering
        MockHttpSession session = new MockHttpSession();
        Resource user = new Resource();
        user.setResourceId(1);
        user.setName("Test User");
        session.setAttribute("loggedInUser", user);

        TimeRegistration existingTimeReg = new TimeRegistration();
        existingTimeReg.setTimeId(1);
        existingTimeReg.setTaskId(1);

        Resource testResource = new Resource();
        testResource.setResourceId(1);
        testResource.setName("Test Resource");

        // Mocker eksisterende data
        when(timeRegistrationService.getTimeRegistrationById(1)).thenReturn(existingTimeReg);
        when(resourceService.getAllResources()).thenReturn(List.of(testResource));

        // Act & Assert - Opdaterer tidsregistrering (fra 4.0 til 6.0 timer)
        mockMvc.perform(post("/timeregistrations/1/update")
                .session(session)
                .param("timeId", "1")
                .param("taskId", "1")
                .param("resourceId", "1")
                .param("date", "2024-01-01")
                .param("hours", "6.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/1"));

        // Verificerer at opdatering blev udført
        verify(timeRegistrationService, times(1)).updateTimeRegistration(any(TimeRegistration.class));
    }
}