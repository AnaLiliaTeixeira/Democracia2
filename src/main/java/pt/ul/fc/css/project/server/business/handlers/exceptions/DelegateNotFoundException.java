package pt.ul.fc.css.project.server.business.handlers.exceptions;

/** Thrown when a delegate is not found in the database. */
public class DelegateNotFoundException extends RuntimeException {


  public DelegateNotFoundException( ) {
    super("Delegate not found.");
  }
}
