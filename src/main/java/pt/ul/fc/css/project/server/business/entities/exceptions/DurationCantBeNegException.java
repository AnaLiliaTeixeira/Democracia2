package pt.ul.fc.css.project.server.business.entities.exceptions;

public class DurationCantBeNegException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DurationCantBeNegException() {
    super("Duration cannot be lower or equal to current date.");
  }
}
