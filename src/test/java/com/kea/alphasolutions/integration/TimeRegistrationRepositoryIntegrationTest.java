package com.kea.alphasolutions.integration;

import java.time.LocalDate;

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

import com.kea.alphasolutions.model.Task;
import com.kea.alphasolutions.model.TimeRegistration;
import com.kea.alphasolutions.repository.TaskRepository;
import com.kea.alphasolutions.repository.TimeRegistrationRepository;
import com.kea.alphasolutions.service.TimeRegistrationService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql("/h2init.sql")
public class TimeRegistrationRepositoryIntegrationTest {

    @Autowired
    private TimeRegistrationRepository timeRegistrationRepository;
    
    @Autowired
    private TimeRegistrationService timeRegistrationService;
    
    @Autowired
    private TaskRepository taskRepository;

    // ========================================
    // TIDSAGGREGERINGSTESTS
    // ========================================

    @Test
    void shouldCalculateTotalHoursByTaskId() {
        // Tester beregning af totale timer for en specifik opgave.
        // Opgave 1 har flere tidsregistreringer i testdata:
        // - 8.00 timer registreret af ressource 1
        // - 2.00 timer registreret af ressource 2
        // Total: 8.00 + 2.00 = 10.00 timer
        double totalHours = timeRegistrationRepository.getTotalHoursByTaskId(1);
        
        // Verificerer at summen er korrekt (10.00 timer med 0.01 tolerance for floating point)
        assertEquals(10.00, totalHours, 0.01);
    }

    @Test
    void shouldCalculateTotalHoursBySubprojectId() {
        // Tester beregning af totale timer for et helt delprojekt.
        // Dette aggregerer timer fra alle opgaver i delprojektet.
        // 
        // Delprojekt 1 (Frontend Development) indeholder opgaver 1,2,3,4:
        // - Opgave 1: 10.00 timer total
        // - Opgave 2: 16.00 timer total
        // - Opgave 3: 7.50 timer total
        // - Opgave 4: 0.00 timer (ingen registreringer)
        // Total: 10.00 + 16.00 + 7.50 = 33.50 timer
        double totalHours = timeRegistrationRepository.getTotalHoursBySubprojectId(1);
        
        assertEquals(33.50, totalHours, 0.01);
    }

    @Test
    void shouldCalculateTotalHoursByProjectId() {
        // Tester beregning af totale timer for et helt projekt.
        // Dette aggregerer timer fra ALLE opgaver i ALLE delprojekter.
        // 
        // Projekt 1 har delprojekter 1,2,3 med forskellige tidsregistreringer.
        // Dette giver projektledere et komplet overblik over ressourceforbruget.
        double totalHours = timeRegistrationRepository.getTotalHoursByProjectId(1);
        
        // Verificerer at der er registreret tid på projektet
        assertTrue(totalHours > 0);
        
        // Sætter en fornuftig øvre grænse baseret på vores testdata
        // for at sikre at beregningen ikke returnerer urimelige værdier
        assertTrue(totalHours < 200);
    }

    @Test
    void shouldReturnZeroForTaskWithNoTimeEntries() {
        // Tester at metoden håndterer opgaver uden tidsregistreringer korrekt.
        // Opgave 4 (Contact Form) har ingen tidsregistreringer i vores testdata,
        // så metoden skal returnere 0.0 timer i stedet for null eller fejl.
        double totalHours = timeRegistrationRepository.getTotalHoursByTaskId(4);
        
        assertEquals(0.0, totalHours, 0.01);
    }

    // ========================================
    // AUTOMATISK STATUS OPDATERING INTEGRATIONSTESTS
    // ========================================

    @Test
    void shouldUpdateTaskStatusToInProgressWhenTimeAdded() {
        // Tester automatisk statusopdatering når tid registreres på en opgave.
        // Når en medarbejder begynder at arbejde på en TO_DO opgave,
        // skal systemet automatisk ændre status til IN_PROGRESS.
        
        // Henter en opgave med TO_DO status (Opgave 4 - Contact Form)
        Task todoTask = taskRepository.findById(4);
        assertNotNull(todoTask);
        assertEquals("TO_DO", todoTask.getStatus());
        
        // Tilføjer tidsregistrering via service (som inkluderer auto-status logik).
        // Vi bruger service i stedet for repository direkte, fordi det er servicen
        // der håndterer forretningslogikken omkring statusopdatering.
        TimeRegistration newTimeReg = new TimeRegistration();
        newTimeReg.setTaskId(4);
        newTimeReg.setResourceId(1);
        newTimeReg.setDate(LocalDate.now());
        newTimeReg.setHours(2.5);
        
        timeRegistrationService.addTimeRegistration(newTimeReg);
        
        // Verificerer at opgavens status automatisk blev ændret til IN_PROGRESS
        Task updatedTask = taskRepository.findById(4);
        assertEquals("IN_PROGRESS", updatedTask.getStatus());
        
        // Verificerer også at tiden faktisk blev gemt i databasen
        double totalHours = timeRegistrationRepository.getTotalHoursByTaskId(4);
        assertEquals(2.5, totalHours, 0.01);
    }

    @Test
    void shouldNotChangeStatusFromInProgressToDone() {
        // Tester at systemet IKKE automatisk ændrer status fra IN_PROGRESS til DONE.
        // Selvom der registreres mere tid på en opgave, skal den forblive IN_PROGRESS.
        // Kun projektlederen eller den ansvarlige skal kunne markere en opgave som færdig.
        
        // Henter en opgave der allerede er IN_PROGRESS (Opgave 3 fra testdata)
        Task inProgressTask = taskRepository.findById(3);
        assertNotNull(inProgressTask);
        assertEquals("IN_PROGRESS", inProgressTask.getStatus());
        
        // Tilføjer yderligere tid på opgaven (3 timer mere)
        TimeRegistration additionalTime = new TimeRegistration();
        additionalTime.setTaskId(3);
        additionalTime.setResourceId(2);
        additionalTime.setDate(LocalDate.now());
        additionalTime.setHours(3.0);
        
        timeRegistrationService.addTimeRegistration(additionalTime);
        
        // Status skal forblive IN_PROGRESS og IKKE automatisk skifte til DONE.
        // Dette sikrer at opgaver ikke lukkes før de faktisk er færdige.
        Task stillInProgressTask = taskRepository.findById(3);
        assertEquals("IN_PROGRESS", stillInProgressTask.getStatus());
    }

    @Test
    void shouldNotChangeStatusFromDoneToInProgress() {
        // Tester at en færdig opgave IKKE går tilbage til IN_PROGRESS status.
        // Nogle gange skal der registreres ekstra tid på færdige opgaver
        // (f.eks. oprydning, dokumentation, eller små rettelser),
        // men opgaven skal forblive markeret som DONE.
        
        // Henter en opgave der allerede er DONE (Opgave 1 fra testdata)
        Task doneTask = taskRepository.findById(1);
        assertNotNull(doneTask);
        assertEquals("DONE", doneTask.getStatus());
        
        // Tilføjer ekstra tid (1 time) - måske til oprydningsarbejde
        TimeRegistration additionalTime = new TimeRegistration();
        additionalTime.setTaskId(1);
        additionalTime.setResourceId(3);
        additionalTime.setDate(LocalDate.now());
        additionalTime.setHours(1.0);
        
        timeRegistrationService.addTimeRegistration(additionalTime);
        
        // Status skal forblive DONE og ikke regrediere til IN_PROGRESS.
        // Dette beskytter mod utilsigtet genåbning af lukkede opgaver.
        Task stillDoneTask = taskRepository.findById(1);
        assertEquals("DONE", stillDoneTask.getStatus());
    }

    // ========================================
    // CRUD OPERATIONER TEST
    // ========================================

    @Test
    void shouldCreateUpdateAndDeleteTimeRegistration() {
        // Tester komplet CRUD (Create, Read, Update, Delete) funktionalitet
        // for tidsregistreringer. Dette sikrer at alle grundlæggende
        // database operationer fungerer korrekt.
        
        // CREATE: Opretter en ny tidsregistrering med 4.5 timer
        TimeRegistration newTimeReg = new TimeRegistration();
        newTimeReg.setTaskId(2);
        newTimeReg.setResourceId(2);
        newTimeReg.setDate(LocalDate.now());
        newTimeReg.setHours(4.5);
        
        timeRegistrationRepository.save(newTimeReg);
        
        // READ: Finder den gemte registrering ved at søge på opgave ID.
        // Vi filtrerer på timer (4.5) for at finde netop vores registrering,
        // da der kan være andre registreringer på samme opgave.
        var timeRegs = timeRegistrationRepository.findAllByTaskId(2);
        TimeRegistration savedReg = timeRegs.stream()
                .filter(tr -> tr.getHours() == 4.5)
                .findFirst()
                .orElse(null);
        
        // Verificerer at registreringen blev gemt korrekt
        assertNotNull(savedReg);
        assertEquals(2, savedReg.getTaskId());
        assertEquals(2, savedReg.getResourceId());
        
        // UPDATE: Ændrer antallet af timer fra 4.5 til 6.0
        savedReg.setHours(6.0);
        timeRegistrationRepository.update(savedReg);
        
        // Verificerer at opdateringen blev gemt i databasen
        TimeRegistration updatedReg = timeRegistrationRepository.findById(savedReg.getTimeId());
        assertEquals(6.0, updatedReg.getHours(), 0.01);
        
        // DELETE: Sletter tidsregistreringen fra databasen
        timeRegistrationRepository.delete(savedReg.getTimeId());
        
        // Verificerer at registreringen er blevet slettet.
        // findById skal returnere null for ikke-eksisterende registreringer.
        TimeRegistration deletedReg = timeRegistrationRepository.findById(savedReg.getTimeId());
        assertNull(deletedReg);
    }
}