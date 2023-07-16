package pt.ul.fc.css.project.server.business.entities;

import static jakarta.persistence.InheritanceType.JOINED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.lang.NonNull;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;

@Entity
@Inheritance(strategy = JOINED)
/** Class to creat objects of Citizens */
public abstract class Citizen {

  @Id
  protected Long id; // primary key

  @NonNull protected String name;

  @ElementCollection(fetch = FetchType.EAGER)
  // Map<THEME_ID, DELEGATE_ID> to keep the delegates of the citizen
  protected Map<Long, Long> delegate_per_theme = new HashMap<>();

  @ElementCollection(fetch = FetchType.EAGER)
  // List<PROJECT_ID> list of projects voted
  protected List<Long> projects_voted = new ArrayList<>();

  @ElementCollection(fetch = FetchType.EAGER)
  // List<PROJECT_ID> list of projects signed
  protected List<Long> projects_signed = new ArrayList<>();

  /** Constructor */
  @SuppressWarnings("null")
  public Citizen() {}

  /**
   * Constructor
   *
   * @param name - the name of the citizen
   */
  public Citizen(@NonNull String name) {
    this.name = name;
  }

  /**
   * Getter for the name of the citizen
   *
   * @return the name of the citizen
   */
  public String getName() {
    return name;
  }

  public void setName(@NonNull String name) {
    this.name = name;
  }

  /**
   * Return the list of delegates per theme
   *
   * @return returns a map of delegates per theme
   */
  public Map<Long, Long> getDelegate_per_theme() {
    return delegate_per_theme;
  }

  /**
   * Returns the list of projects voted
   *
   * @return returns a list of long with the ids of the projects
   */
  public List<Long> getProjects_voted() {
    return projects_voted;
  }

  /**
   * Returns the id of the citizen
   *
   * @return returns the id of the citizen
   */
  public Long getId() {
    return id;
  }

  public void setId(Long citizenId) {
    this.id = citizenId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Citizen other = (Citizen) obj;
    return Objects.equals(id, other.id) && Objects.equals(name, other.name);
  }

  @Override
  public String toString() {
    return "Citizen [id="
        + id
        + ", name="
        + name
        + ", delegate_per_theme="
        + delegate_per_theme
        + ", project_voted="
        + projects_voted
        + "]";
  }

  /**
   * Adds project id to the list of projects signed
   *
   * @param lawProject_id the id of the project
   */
  public void add_law_project_sign(Long lawProject_id) {
    this.projects_signed.add(lawProject_id);
  }

  /**
   * Adds delegate to a theme
   *
   * @param theme_id its the id of the theme
   * @param delegate_id its the delegate_id
   */
  public void add_del_to_theme(Long theme_id, Long delegate_id) {
    this.delegate_per_theme.put(theme_id, delegate_id);
  }

  /**
   * Checks if the project was already voted by the citizen
   *
   * @param lawProject_id the id of the lawProject
   * @return true if the project was already voted or false if its not
   */
  public boolean alreadyVoted(Long lawProject_id) {
    return this.projects_voted.contains(lawProject_id);
  }

  /**
   * Returns the delegate of the theme associated to the citizen
   *
   * @param theme_id its the id of the theme
   * @return returns the delegate id associated to the citizen for the theme
   */
  public Long getDelegate(Long theme_id) {
    if(delegate_per_theme.containsKey(theme_id))
      return this.delegate_per_theme.get(theme_id);
    else
      return -1L; // citizen has no delegate associated with that theme
  }

  /**
   * Checks if the citizen has a delegate for the theme
   *
   * @param theme_id its the id of the theme
   * @return true if the theme has a delegate associated
   */
  public boolean hasDelegate(Long theme_id) {
    return this.delegate_per_theme.containsKey(theme_id);
  }

  public boolean alreadySigned(Long lawProject_id) {
    return this.projects_signed.contains(lawProject_id);
  }

}
