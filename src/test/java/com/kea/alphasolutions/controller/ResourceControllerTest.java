package com.kea.alphasolutions.controller;

import com.kea.alphasolutions.model.Resource;
import com.kea.alphasolutions.service.ResourceService;
import com.kea.alphasolutions.service.TimeRegistrationService;
import com.kea.alphasolutions.service.TaskService;
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

@WebMvcTest(ResourceController.class)
public class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResourceService resourceService;

    @MockitoBean
    private TimeRegistrationService timeRegistrationService;

    @MockitoBean
    private TaskService taskService;

    @Test
    void regularUserCanViewResourceList() throws Exception {
        // Arrange - Opsætter almindelig bruger (ikke admin)
        MockHttpSession session = new MockHttpSession();
        Resource regularUser = new Resource();
        regularUser.setResourceId(1);
        regularUser.setName("John Doe");
        regularUser.setSystemRole("USER");
        session.setAttribute("loggedInUser", regularUser);

        Resource resource1 = new Resource();
        resource1.setResourceId(1);
        resource1.setName("Resource 1");
        
        when(resourceService.getAllResources()).thenReturn(List.of(resource1));

        // Act & Assert - Verificerer at almindelige brugere kan se ressourcelisten
        mockMvc.perform(get("/resources").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("resource/list"))
                .andExpect(model().attributeExists("resources"));

        verify(resourceService, times(1)).getAllResources();
    }

    @Test
    void adminCanAccessResourceCreationForm() throws Exception {
        // Arrange - Opsætter admin bruger
        MockHttpSession session = new MockHttpSession();
        Resource adminUser = new Resource();
        adminUser.setResourceId(1);
        adminUser.setName("Admin User");
        adminUser.setSystemRole("ADMIN");
        session.setAttribute("loggedInUser", adminUser);

        // Act & Assert - Admin kan tilgå formular til oprettelse
        mockMvc.perform(get("/resources/new").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("resource/form"))
                .andExpect(model().attributeExists("resource"));
    }

    @Test
    void regularUserCannotAccessResourceCreationForm() throws Exception {
        // Arrange - Opsætter almindelig bruger
        MockHttpSession session = new MockHttpSession();
        Resource regularUser = new Resource();
        regularUser.setResourceId(2);
        regularUser.setName("Regular User");
        regularUser.setSystemRole("USER");
        session.setAttribute("loggedInUser", regularUser);

        // Act & Assert - Almindelig bruger bliver omdirigeret til login
        mockMvc.perform(get("/resources/new").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void adminCanCreateResource() throws Exception {
        // Arrange - Admin bruger opsætning
        MockHttpSession session = new MockHttpSession();
        Resource adminUser = new Resource();
        adminUser.setResourceId(1);
        adminUser.setName("Admin User");
        adminUser.setSystemRole("ADMIN");
        session.setAttribute("loggedInUser", adminUser);

        // Act & Assert - Admin kan oprette ny ressource
        mockMvc.perform(post("/resources")
                .session(session)
                .param("name", "New Resource")
                .param("role", "Developer")
                .param("email", "new@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/resources"));

        // Verificerer at ressource blev tilføjet
        verify(resourceService, times(1)).addResource(any(Resource.class));
    }

    @Test
    void regularUserCannotCreateResource() throws Exception {
        // Arrange - Almindelig bruger forsøger at oprette
        MockHttpSession session = new MockHttpSession();
        Resource regularUser = new Resource();
        regularUser.setResourceId(2);
        regularUser.setName("Regular User");
        regularUser.setSystemRole("USER");
        session.setAttribute("loggedInUser", regularUser);

        // Act & Assert - Almindelig bruger blokeres og omdirigeres
        mockMvc.perform(post("/resources")
                .session(session)
                .param("name", "New Resource")
                .param("role", "Developer")
                .param("email", "new@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        // Verificerer at ressource IKKE blev oprettet
        verify(resourceService, never()).addResource(any(Resource.class));
    }

    @Test
    void adminCanAccessResourceEditForm() throws Exception {
        // Arrange - Admin tilgår redigeringsformular
        MockHttpSession session = new MockHttpSession();
        Resource adminUser = new Resource();
        adminUser.setResourceId(1);
        adminUser.setName("Admin User");
        adminUser.setSystemRole("ADMIN");
        session.setAttribute("loggedInUser", adminUser);

        Resource existingResource = new Resource();
        existingResource.setResourceId(3);
        existingResource.setName("Existing Resource");
        when(resourceService.getResourceById(3)).thenReturn(existingResource);

        // Act & Assert - Admin kan redigere eksisterende ressource
        mockMvc.perform(get("/resources/3/edit").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("resource/form"))
                .andExpect(model().attributeExists("resource"));

        verify(resourceService, times(1)).getResourceById(3);
    }

    @Test
    void regularUserCannotAccessResourceEditForm() throws Exception {
        // Arrange - Almindelig bruger blokeres fra redigering
        MockHttpSession session = new MockHttpSession();
        Resource regularUser = new Resource();
        regularUser.setResourceId(2);
        regularUser.setName("Regular User");
        regularUser.setSystemRole("USER");
        session.setAttribute("loggedInUser", regularUser);

        // Act & Assert - Omdirigeres uden at hente ressource
        mockMvc.perform(get("/resources/3/edit").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(resourceService, never()).getResourceById(anyInt());
    }

    @Test
    void adminCanUpdateResource() throws Exception {
        // Arrange - Admin opdaterer ressource
        MockHttpSession session = new MockHttpSession();
        Resource adminUser = new Resource();
        adminUser.setResourceId(1);
        adminUser.setName("Admin User");
        adminUser.setSystemRole("ADMIN");
        session.setAttribute("loggedInUser", adminUser);

        // Act & Assert - Opdatering udføres og omdirigerer
        mockMvc.perform(post("/resources/3/update")
                .session(session)
                .param("name", "Updated Resource")
                .param("role", "Senior Developer")
                .param("email", "updated@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/resources"));

        verify(resourceService, times(1)).updateResource(any(Resource.class));
    }

    @Test
    void regularUserCannotUpdateResource() throws Exception {
        // Arrange - Almindelig bruger forsøger opdatering
        MockHttpSession session = new MockHttpSession();
        Resource regularUser = new Resource();
        regularUser.setResourceId(2);
        regularUser.setName("Regular User");
        regularUser.setSystemRole("USER");
        session.setAttribute("loggedInUser", regularUser);

        // Act & Assert - Opdatering blokeres
        mockMvc.perform(post("/resources/3/update")
                .session(session)
                .param("name", "Updated Resource")
                .param("role", "Senior Developer")
                .param("email", "updated@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        // Verificerer at opdatering IKKE skete
        verify(resourceService, never()).updateResource(any(Resource.class));
    }

    @Test
    void adminCanDeleteResource() throws Exception {
        // Arrange - Admin sletter ressource
        MockHttpSession session = new MockHttpSession();
        Resource adminUser = new Resource();
        adminUser.setResourceId(1);
        adminUser.setName("Admin User");
        adminUser.setSystemRole("ADMIN");
        session.setAttribute("loggedInUser", adminUser);

        // Act & Assert - Sletning udføres succesfuldt
        mockMvc.perform(get("/resources/3/delete").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/resources"));

        verify(resourceService, times(1)).deleteResource(3);
    }

    @Test
    void regularUserCannotDeleteResource() throws Exception {
        // Arrange - Almindelig bruger forsøger sletning
        MockHttpSession session = new MockHttpSession();
        Resource regularUser = new Resource();
        regularUser.setResourceId(2);
        regularUser.setName("Regular User");
        regularUser.setSystemRole("USER");
        session.setAttribute("loggedInUser", regularUser);

        // Act & Assert - Sletning blokeres og bruger omdirigeres
        mockMvc.perform(get("/resources/3/delete").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        // Verificerer at sletning IKKE skete
        verify(resourceService, never()).deleteResource(anyInt());
    }
}