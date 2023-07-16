package pt.ul.fc.css.project.server.business.handlers.exceptions;

/** The exception is thrown when a law_project is not found in the database. */
public class LawProjectNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public LawProjectNotFoundException(Object id) {
    super("LawProject with ID = " + id + " not found");
  }
}
