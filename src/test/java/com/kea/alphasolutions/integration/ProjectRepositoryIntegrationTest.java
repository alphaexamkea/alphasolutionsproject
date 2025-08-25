package com.kea.alphasolutions.integration;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.kea.alphasolutions.model.Project;
import com.kea.alphasolutions.repository.ProjectRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql("/h2init.sql")
public class ProjectRepositoryIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void shouldLoadTestData() {
        // Verificerer at testdata fra h2init.sql filen er blevet indlæst korrekt i databasen.
        // Dette sikrer at vores test-database er korrekt initialiseret med de forventede
        // projekter, så vi kan teste mod kendte data.
        List<Project> projects = projectRepository.findAll();
        
        // Tjekker at præcis 3 projekter er blevet indlæst som forventet
        assertEquals(3, projects.size());
        
        // Verificerer at alle tre specifikke projekter fra vores testdata eksisterer
        // ved at søge gennem listen og tjekke om hvert projekt navn er til stede
        assertTrue(projects.stream().anyMatch(p -> "Alpha Solutions Website".equals(p.getName())));
        assertTrue(projects.stream().anyMatch(p -> "Mobile App Development".equals(p.getName())));
        assertTrue(projects.stream().anyMatch(p -> "Data Analytics Platform".equals(p.getName())));
    }

    @Test
    void shouldFindProjectById() {
        // Tester at vi kan finde et specifikt projekt baseret på dets ID.
        // Vi bruger projekt ID 1 som vi ved eksisterer i vores testdata.
        Project project = projectRepository.findById(1);
        
        // Verificerer at projektet blev fundet (ikke null)
        assertNotNull(project);
        
        // Tjekker at alle projektets felter har de korrekte værdier fra testdata.
        // Dette sikrer at vores findById metode returnerer komplet og korrekt data.
        assertEquals("Alpha Solutions Website", project.getName());
        assertEquals("Company website redesign project", project.getDescription());
        assertEquals(LocalDate.of(2024, 1, 1), project.getStartDate());
        assertEquals(LocalDate.of(2024, 6, 30), project.getEndDate());
    }

    @Test
    void shouldCreateNewProject() {
        // Gemmer det oprindelige antal projekter før vi opretter et nyt.
        // Dette giver os mulighed for at verificere at antallet stiger med 1.
        List<Project> initialProjects = projectRepository.findAll();
        int initialCount = initialProjects.size();
        
        // Opretter et nyt projekt objekt med testdata.
        // Bruger LocalDate.now() for dynamiske datoer og plusMonths(6) for at
        // sætte slutdatoen 6 måneder frem i tiden.
        Project newProject = new Project();
        newProject.setName("Test Project");
        newProject.setDescription("A project created during testing");
        newProject.setStartDate(LocalDate.now());
        newProject.setEndDate(LocalDate.now().plusMonths(6));

        // Gemmer projektet i databasen via repository
        projectRepository.save(newProject);
        
        // Verificerer at projektet blev gemt ved at tjekke at antallet af projekter
        // er steget med præcis 1
        List<Project> allProjects = projectRepository.findAll();
        assertEquals(initialCount + 1, allProjects.size());
        
        // Finder det nyoprettede projekt i listen ved at søge efter navnet.
        // Bruger Java streams til at filtrere listen og finde projektet.
        Project savedProject = allProjects.stream()
                .filter(p -> "Test Project".equals(p.getName()))
                .findFirst()
                .orElse(null);
        
        // Verificerer at projektet blev fundet og at alle felter blev gemt korrekt
        assertNotNull(savedProject);
        assertEquals("Test Project", savedProject.getName());
        assertEquals("A project created during testing", savedProject.getDescription());
    }

    @Test
    void shouldUpdateExistingProject() {
        // Henter et eksisterende projekt fra databasen (projekt med ID 1).
        // Vi ved dette projekt eksisterer fra vores testdata.
        Project project = projectRepository.findById(1);
        assertNotNull(project);

        // Opdaterer projektets navn og beskrivelse med nye værdier.
        // Vi ændrer kun disse to felter for at teste update funktionaliteten.
        project.setName("Updated Website Project");
        project.setDescription("Updated description");
        
        // Kalder update metoden for at gemme ændringerne i databasen
        projectRepository.update(project);
        
        // Henter projektet igen fra databasen for at verificere at ændringerne
        // blev gemt korrekt. Dette sikrer at update metoden faktisk opdaterer
        // dataene i databasen og ikke kun i memory.
        Project updatedProject = projectRepository.findById(1);
        assertEquals("Updated Website Project", updatedProject.getName());
        assertEquals("Updated description", updatedProject.getDescription());
    }

    @Test
    void shouldDeleteProject() {
        // Opretter et nyt projekt specifikt til denne test, så vi ikke påvirker
        // eksisterende testdata. Dette projekt vil blive slettet som del af testen.
        Project projectToDelete = new Project();
        projectToDelete.setName("Project To Delete");
        projectToDelete.setDescription("This project will be deleted");
        projectToDelete.setStartDate(LocalDate.now());
        projectToDelete.setEndDate(LocalDate.now().plusMonths(3));

        // Gemmer projektet i databasen
        projectRepository.save(projectToDelete);
        
        // Finder det oprettede projekt for at få dets auto-genererede ID.
        // Vi kan ikke vide ID'et på forhånd da det genereres af databasen.
        List<Project> allProjects = projectRepository.findAll();
        Project createdProject = allProjects.stream()
                .filter(p -> "Project To Delete".equals(p.getName()))
                .findFirst()
                .orElse(null);
        
        // Verificerer at projektet blev oprettet og gemmer dets ID
        assertNotNull(createdProject);
        int projectId = createdProject.getProjectId();
        
        // Dobbelt-tjekker at projektet eksisterer i databasen før vi sletter det
        assertNotNull(projectRepository.findById(projectId));
        
        // Sletter projektet fra databasen ved hjælp af dets ID
        projectRepository.delete(projectId);
        
        // Verificerer at projektet er blevet slettet ved at søge efter det igen.
        // findById skal returnere null når projektet ikke længere eksisterer.
        assertNull(projectRepository.findById(projectId));
    }

    @Test
    void shouldCountSubprojectsCorrectly() {
        // Tester analytics-metoden der tæller antal delprojekter for hvert projekt.
        // Dette er vigtig funktionalitet for projektledere der skal have overblik
        // over projektets kompleksitet og struktur.
        
        // Projekt 1 (Alpha Solutions Website) skal have 3 delprojekter ifølge vores testdata:
        // Frontend Development, Backend Development, og Database Design
        int subprojectCount = projectRepository.countSubprojectsByProjectId(1);
        assertEquals(3, subprojectCount);
        
        // Projekt 2 (Mobile App Development) skal også have 3 delprojekter
        int project2Count = projectRepository.countSubprojectsByProjectId(2);
        assertEquals(3, project2Count);
        
        // Projekt 3 (Data Analytics Platform) skal have 2 delprojekter
        int project3Count = projectRepository.countSubprojectsByProjectId(3);
        assertEquals(2, project3Count);
    }

    @Test
    void shouldCountTasksCorrectly() {
        // Tester optælling af alle opgaver på tværs af alle delprojekter i et projekt.
        // Dette giver projektledere et samlet overblik over arbejdsbyrden.
        // 
        // Projekt 1 har 3 delprojekter med følgende opgaver:
        // - Delprojekt 1 (Frontend): 4 opgaver
        // - Delprojekt 2 (Backend): 3 opgaver  
        // - Delprojekt 3 (Database): 2 opgaver
        // Total: 4 + 3 + 2 = 9 opgaver
        int taskCount = projectRepository.countTasksByProjectId(1);
        
        assertEquals(9, taskCount);
    }

    @Test
    void shouldCountResourcesCorrectly() {
        // Tester optælling af unikke ressourcer (medarbejdere) der arbejder på et projekt.
        // Metoden skal tælle hver ressource kun én gang, selvom de har registreret tid
        // på flere forskellige opgaver inden for samme projekt.
        // 
        int resourceCount = projectRepository.countResourcesByProjectId(1);
        
        // Verificerer at mindst én ressource arbejder på projektet
        assertTrue(resourceCount > 0);
        
        // Verificerer at antallet ikke overstiger det maksimale antal ressourcer
        // i vores testdata (vi har kun 4 ressourcer total i h2init.sql)
        assertTrue(resourceCount <= 4);
    }
}