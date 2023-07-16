package pt.ul.fc.css.project.server.business.entities;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import org.springframework.lang.NonNull;

/** The class that creates objects for the theme */
@Entity
public class Theme {

  @NonNull private String name; // the name of the thme

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id; // primary key

  @ManyToOne private Theme parent; // the parent of the current theme

  @ElementCollection
  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "children")
  private List<Theme> children = new ArrayList<Theme>(); // list of childrens of the theme

  @SuppressWarnings("null")
  public Theme() {}

  /**
   * Constructor
   *
   * @param name the name of the theme
   */
  public Theme(@NonNull String name) {
    this.name = name;
  }

  /**
   * Add a subtheme to another theme
   *
   * @param theme the theme to be added
   * @return the Theme "theme"
   */
  public Theme addTheme(Theme theme) {
    theme.parent = this;
    this.children.add(theme);
    return theme;
  }

  /**
   * Gets the name of the theme
   *
   * @return the string with the name of the theme
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the id of the theme
   *
   * @return the long with the id of the theme
   */
  public Long getId() {
    return id;
  }

  /**
   * Gets the parent of the theme
   *
   * @return the parent of the theme
   */
  public Theme getParent() {
    return parent;
  }

  /** Gets the list of children */
  public List<Theme> getThemes() {
    return this.children;
  }
}
