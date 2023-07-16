package pt.ul.fc.css.project.server.business.handlers.exceptions;

/** The exception is thrown when a citizen is not a delegate. */
public class CitizenIsNotDelegateException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CitizenIsNotDelegateException(Long id) {
    super("Citizen with ID = " + id + " is not a delegate.");
  }
}
