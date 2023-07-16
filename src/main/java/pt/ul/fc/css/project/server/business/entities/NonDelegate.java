package pt.ul.fc.css.project.server.business.entities;

import jakarta.persistence.Entity;
import org.springframework.lang.NonNull;

@Entity
/** Class that extends Citizens and creates Non delegates */
public class NonDelegate extends Citizen {

  public NonDelegate() {}

  public NonDelegate(@NonNull String name) {
    super(name);
  }

  /**
   * Adds a private vote to the list of votes of the citizen.
   *
   * @param lawProject_id the id of the law project
   */
  public void votes(Long lawProject_id) {
    this.projects_voted.add(lawProject_id);
  }

  public boolean checkIfVotedOnAlawProject(Long lawProject_id) {
      return this.projects_voted.contains(lawProject_id);
  }
}
