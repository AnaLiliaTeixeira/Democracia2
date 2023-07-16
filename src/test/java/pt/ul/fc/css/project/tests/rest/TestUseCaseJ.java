package pt.ul.fc.css.project.tests.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pt.ul.fc.css.project.server.DemocraciaApplication;
import pt.ul.fc.css.project.server.business.entities.Delegate;
import pt.ul.fc.css.project.server.business.entities.NonDelegate;
import pt.ul.fc.css.project.server.business.entities.Theme;
import pt.ul.fc.css.project.server.business.repositories.interfaces.CitizenRepositoryInterface;
import pt.ul.fc.css.project.server.business.repositories.interfaces.ThemeRepositoryInterface;
import pt.ul.fc.css.project.server.business.services.LawProjectService;
import pt.ul.fc.css.project.server.business.services.dtos.LawProjectDTO;
import pt.ul.fc.css.project.server.controllers.RestLawProjectController;

@AutoConfigureMockMvc
@SpringBootTest(classes = DemocraciaApplication.class)
public class TestUseCaseJ {

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

    Delegate vasco;
    NonDelegate ana;

    LocalDateTime localDateTime;

    Theme theme;

    @BeforeEach
    public void load() {
        vasco = new Delegate("Vasco");
        vasco.setId(1L);
        ana = new NonDelegate("Ana");
        ana.setId(2L);
        cRep.save(vasco);
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
    public void testVoteOnLawProject() {
        try {
            LawProjectDTO lawProjectDTO = lawProjectService.add_law_project("Law Project 103", "Description 103", new String("pdf_attachement"), localDateTime, vasco.getId(), theme.getId());
            lawProjectService.set_sign(lawProjectDTO.getId());


            mockMvc.perform(MockMvcRequestBuilders.post("/api/lawprojectsvotations/{lawProject_id}/vote", lawProjectDTO.getId())
                .param("citizen_id", String.valueOf(ana.getId()))
                .param("vote", "True"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Voted successfully"));

            mockMvc.perform(MockMvcRequestBuilders.post("/api/lawprojectsvotations/{lawProject_id}/vote", lawProjectDTO.getId())
                .param("citizen_id", String.valueOf(ana.getId()))
                .param("vote", "True"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string("You already voted on this project"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
