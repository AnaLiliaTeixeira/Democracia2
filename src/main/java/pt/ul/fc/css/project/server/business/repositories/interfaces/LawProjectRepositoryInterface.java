package pt.ul.fc.css.project.server.business.repositories.interfaces;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pt.ul.fc.css.project.server.business.entities.LawProject;

/** The repository to deal with the law projects objects */
@Repository
public interface LawProjectRepositoryInterface extends JpaRepository<LawProject, Long> {

  @Query("SELECT lp FROM LawProject lp WHERE lp.closed = FALSE ")
  List<LawProject> findNonExpiredProjects();

  @Query("SELECT lp FROM LawProject lp WHERE lp.closed = TRUE ")
  List<LawProject> findExpiredProjects();

  @Query("SELECT lp FROM LawProject lp WHERE lp.votation.isActive = FALSE AND lp.closed = TRUE")
  List<LawProject> findClosedVotations();

  @Query("SELECT lp FROM LawProject lp WHERE lp.closed = FALSE AND lp.votation.isActive = TRUE")
  List<LawProject> findCurrentVotations();
}
