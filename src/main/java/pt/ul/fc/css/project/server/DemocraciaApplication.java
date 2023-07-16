package pt.ul.fc.css.project.server;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import pt.ul.fc.css.project.server.business.entities.Citizen;
import pt.ul.fc.css.project.server.business.entities.Delegate;
import pt.ul.fc.css.project.server.business.entities.LawProject;
import pt.ul.fc.css.project.server.business.entities.NonDelegate;
import pt.ul.fc.css.project.server.business.entities.Theme;

@SpringBootApplication
@EnableScheduling
public class DemocraciaApplication extends SpringBootServletInitializer {

  @Autowired
  EntityManagerFactory factory;

  public static void main(String[] args) {
    SpringApplication.run(DemocraciaApplication.class, args);
  }

  @Profile("!test")
  @Bean
  public CommandLineRunner demo() {
    return (args) -> {

      EntityManager em = factory.createEntityManager();
      em.getTransaction().begin();

      Delegate catarina = new Delegate("Catarina");
      catarina.setId(1L);
      NonDelegate vasco = new NonDelegate("Vasco");
      vasco.setId(2L);
      Delegate ana = new Delegate("Ana");
      ana.setId(3L);
      List<Citizen> list1 = Arrays.asList(catarina, vasco, ana);
      list1.forEach(em::persist);

      Theme educacao = new Theme("Educacao");
      Theme escolas = new Theme("Escolas");
      Theme professores = new Theme("Professores");
      educacao.addTheme(escolas);
      escolas.addTheme(professores);

      List<Theme> list2 = Arrays.asList(educacao, escolas, professores);
      list2.forEach(em::persist);

      LocalDateTime now = LocalDateTime.now();
      LocalDateTime d1 = LocalDateTime.now().plusDays(50);
      LocalDateTime d2 = LocalDateTime.now().plusDays(100);
      LocalDateTime d3 = LocalDateTime.now().minusDays(5);

      LawProject lp1 = new LawProject("LawProject1", "Lp1 description", new String("pdf1"), now, catarina.getId(),
          escolas.getId());
      LawProject lp2 = new LawProject("LawProject2", "Lp2 description", new String("pdf2"), d1, ana.getId(),
          professores.getId());
      LawProject lp3 = new LawProject("LawProject3", "Lp3 description", new String("pdf3"), d2, catarina.getId(),
          escolas.getId());
      LawProject lp4 = new LawProject("LawProject4", "Lp4 description", new String("pdf4"), d2, ana.getId(),
          educacao.getId());
      LawProject lp5 = new LawProject("LawProject5", "Lp5 description", new String("pdf5"), d3, catarina.getId(),
          escolas.getId());
      LawProject lp6 = new LawProject("LawProject6", "Lp6 description", new String("pdf6"), d1, ana.getId(),
          professores.getId());

      List<LawProject> list3 = Arrays.asList(lp1, lp2, lp3, lp4, lp5, lp6);
      list3.forEach(em::persist);

      lp1.setSign();
      lp3.setSign();
      lp4.setSign();

      em.persist(lp1);
      em.persist(lp3);
      em.persist(lp4);

      em.getTransaction().commit();
      em.close();
    };
  }
}
