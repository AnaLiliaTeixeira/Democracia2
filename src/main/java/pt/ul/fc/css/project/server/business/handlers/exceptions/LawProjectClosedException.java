package pt.ul.fc.css.project.server.business.handlers.exceptions;

/** The exception is thrown when a law_project is already closed. */
public class LawProjectClosedException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public LawProjectClosedException(Object id) {
    super("Law project with ID = " + id + " is closed.");
  }
}
