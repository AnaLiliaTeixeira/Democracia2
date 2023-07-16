package pt.ul.fc.css.project.server.business.repositories.interfaces;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pt.ul.fc.css.project.server.business.entities.Citizen;
import pt.ul.fc.css.project.server.business.entities.Delegate;

/** The repository to deal with citizen objects */
@Repository
public interface CitizenRepositoryInterface extends JpaRepository<Citizen, Long> {

  @Query("SELECT c FROM Citizen c WHERE :project_id NOT MEMBER OF c.projects_voted")
  List<Citizen> findCitizensWhoDidNotVoteOnLawProject(@Param("project_id") Long project_id);

  @Query("SELECT d FROM Delegate d")
  List<Delegate> findDelegates();
}
