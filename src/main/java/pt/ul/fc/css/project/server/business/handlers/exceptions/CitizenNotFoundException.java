package pt.ul.fc.css.project.server.business.handlers.exceptions;

/** The exception is thrown when a citizen is not found in the database. */
public class CitizenNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CitizenNotFoundException(Long id) {
    super("Citizen with ID = " + id + " not found.");
  }
}
