package pt.ul.fc.css.project.tests.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import pt.ul.fc.css.project.server.DemocraciaApplication;
import pt.ul.fc.css.project.server.business.entities.Delegate;
import pt.ul.fc.css.project.server.business.entities.Theme;
import pt.ul.fc.css.project.server.business.repositories.interfaces.CitizenRepositoryInterface;
import pt.ul.fc.css.project.server.business.repositories.interfaces.ThemeRepositoryInterface;
import pt.ul.fc.css.project.server.business.services.LawProjectService;
import pt.ul.fc.css.project.server.business.services.dtos.LawProjectDTO;
import pt.ul.fc.css.project.server.controllers.RestLawProjectController;

@AutoConfigureMockMvc
@SpringBootTest(classes = DemocraciaApplication.class)
public class TestUseCaseG {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    RestLawProjectController restLawProjectController;

    @Autowired
    LawProjectService lawProjectService;

    @Autowired
    // Repository for themes
    private ThemeRepositoryInterface tRep;
    @Autowired
    // Repository for citizens
    private CitizenRepositoryInterface cRep;

    Delegate ana;
    Theme theme;

    LocalDateTime localDateTime;
    LocalDateTime localDateTime_expired;

    @BeforeEach
    public void load() {
        ana = new Delegate("Ana");
        ana.setId(1L);
        cRep.save(ana);
        theme = new Theme("saude");
        tRep.save(theme);
        localDateTime = LocalDateTime.now().plusDays(1);
    }

    public void contextLoads() throws Exception {
        assertThat(lawProjectService).isNotNull();
        assertThat(restLawProjectController).isNotNull();
        assertThat(lawProjectService.toString()).isNotNull();
        assertThat(restLawProjectController.toString()).isNotNull();
    }

    @Test
    @DirtiesContext
    public void testNonExpiredLawProjects() throws Exception {
        LawProjectDTO lpDTO1 = lawProjectService.add_law_project("Law Project 23", "Description 23", "pdf_attachement",
                localDateTime, ana.getId(), theme.getId());
        LawProjectDTO lpDTO2 = lawProjectService.add_law_project("Law Project 24", "Description 24", "pdf_attachement",
                localDateTime, ana.getId(), theme.getId());

        lpDTO2 = lawProjectService.force_close(lpDTO2.getId());

        waitTenSeconds();

        // Make a GET request to the /api/lawprojects_votation endpoint
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/lawprojects/nonexpired")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<LawProjectDTO> lawProjectsVotation = parseLawProjects(result.getResponse().getContentAsString());

        assertEquals(1, lawProjectsVotation.size());
        assertTrue(lawProjectsVotation.contains(lpDTO1));
    }

    private List<LawProjectDTO> parseLawProjects(String jsonResponse) throws IOException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // Register the JavaTimeModule for LocalDateTime support
            return objectMapper.readValue(jsonResponse, new TypeReference<List<LawProjectDTO>>() {
            });
        } catch (IOException e) {
            throw new IOException("Failed to get Law Projects.");
        }
    }

    public static void waitTenSeconds() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.schedule(() -> {
        }, 10, TimeUnit.SECONDS);

        executor.shutdown();
    }
}
