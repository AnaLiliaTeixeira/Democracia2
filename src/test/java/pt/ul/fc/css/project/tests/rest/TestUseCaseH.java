package pt.ul.fc.css.project.tests.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pt.ul.fc.css.project.server.DemocraciaApplication;
import pt.ul.fc.css.project.server.business.entities.Delegate;
import pt.ul.fc.css.project.server.business.entities.NonDelegate;
import pt.ul.fc.css.project.server.business.entities.Theme;
import pt.ul.fc.css.project.server.business.repositories.interfaces.CitizenRepositoryInterface;
import pt.ul.fc.css.project.server.business.repositories.interfaces.ThemeRepositoryInterface;
import pt.ul.fc.css.project.server.business.services.CitizenService;
import pt.ul.fc.css.project.server.business.services.LawProjectService;
import pt.ul.fc.css.project.server.business.services.dtos.LawProjectDTO;
import pt.ul.fc.css.project.server.controllers.RestLawProjectController;

@AutoConfigureMockMvc
@SpringBootTest(classes = DemocraciaApplication.class)
public class TestUseCaseH {

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

    @Autowired
    CitizenService citizenService;

    Delegate ana;
    NonDelegate vasco;
    Theme theme;

    LocalDateTime localDateTime;

    @BeforeEach
    public void load() {
        ana = new Delegate("Ana");
        ana.setId(1L);
        cRep.save(ana);
        vasco = new NonDelegate("Vasco");
        vasco.setId(2L);
        cRep.save(vasco);
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
    public void testSignLawProject() throws Exception{

        LawProjectDTO lpDTO = lawProjectService.add_law_project("Law Project 104", "Description 4", "pdf_attachement",
                localDateTime, ana.getId(), theme.getId());

        citizenService.set_current_citizen_id(2L);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/lawproject/support/{lawProject_id}", lpDTO.getId())
        .param("lawProjectId", String.valueOf(ana.getId()))
        .param("citizenId", vasco.getId().toString()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string("You have successfully supported this project"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/lawproject/support/{lawProject_id}", lpDTO.getId())
        .param("lawProjectId", String.valueOf(ana.getId()))
        .param("citizenId", "true"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.content().string("You have already signed this project"));

    }
}
