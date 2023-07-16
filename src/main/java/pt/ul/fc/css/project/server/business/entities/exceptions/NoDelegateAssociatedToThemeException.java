package pt.ul.fc.css.project.server.business.entities.exceptions;

/** The exception is thrown when a theme has no delegate associated */
public class NoDelegateAssociatedToThemeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public NoDelegateAssociatedToThemeException(Long delegate_id, Long theme_id) {
    super(
        String.format(
            "Delegate with ID = %d haven't associate theme with ID = %d to any delegate.",
            delegate_id, theme_id));
  }
}
