package pt.ul.fc.css.project.server.business.handlers.exceptions;

public class AlreadSignedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AlreadSignedException() {
        super("You have already signed this project.");
    }
}
