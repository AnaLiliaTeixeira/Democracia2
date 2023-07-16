package pt.ul.fc.css.project.server.business.entities;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import pt.ul.fc.css.project.server.business.entities.enums.Vote;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.lang.NonNull;

/** The Delegate class represents a citizen that can create law projects and have a public vote. */
@Entity
public class Delegate extends Citizen {

  @ElementCollection(fetch = FetchType.EAGER)
  private Map<Long, Vote> list_of_votes = new HashMap<>();

  /** Empty constructor for the Delegate class. */
  public Delegate() {}

  /**
   * Constructor for the Delegate class.
   *
   * @param name the name of the delegate
   */
  public Delegate(@NonNull String name) {
    super(name);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + Objects.hash(list_of_votes);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    Delegate other = (Delegate) obj;
    return Objects.equals(list_of_votes, other.list_of_votes);
  }

  @Override
  public String toString() {
    return "Delegate [list_of_votes=" + list_of_votes + "]";
  }

  /**
   * Adds a public vote to the list of votes of the delegate.
   *
   * @param lawProject_id the id of the law project
   * @param vote the vote of the delegate
   */
  public void votes(long lawProject_id, Vote vote) {
    this.list_of_votes.put(lawProject_id, vote);
    this.projects_voted.add(lawProject_id);
  }

  /**
   * Gets the vote of the delegate for a specific law project.
   *
   * @param lawProject_id the id of the law project
   * @return the vote of the delegate
   */
  public Vote getVoteDel(long lawProject_id) {
    return list_of_votes.get(lawProject_id);
  }

  /**
   * Creates a law project.
   *
   * @param title the title of the law project
   * @param description the description of the law project
   * @param pdf_attachment the pdf attachment of the law project
   * @param closeDate the close date of the law project
   * @param theme_id the id of the theme of the law project
   * @return the law project created
   */
  public LawProject create_law_project(
      String title,
      String description,
      String pdf_attachment,
      LocalDateTime closeDate,
      Long theme_id) {

    LawProject lawp =
        new LawProject(title, description, pdf_attachment, closeDate, this.id, theme_id);
    return lawp;
  }

  /**
   * Gets the public vote of the delegate for a specific law project.
   *
   * @param lawProject_id the id of the law project
   * @return the public vote of the delegate
   */
  public Vote getDelegateVote(Long lawProject_id) {
    return this.list_of_votes.get(lawProject_id);
  }

  public Map<Long, Vote> getList_of_votes() {
    return list_of_votes;
  }

  public boolean checkIfVotedOnAlawProject(Long lawProject_id) {
    return this.list_of_votes.containsKey(lawProject_id);
  }
}
