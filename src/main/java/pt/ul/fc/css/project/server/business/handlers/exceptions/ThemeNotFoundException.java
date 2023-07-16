package pt.ul.fc.css.project.server.business.handlers.exceptions;

/** The exception is thrown when a theme is not found in the database. */
public class ThemeNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ThemeNotFoundException() {
    super("Theme not found.");
  }
}
