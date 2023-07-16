package pt.ul.fc.css.project.server.business.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pt.ul.fc.css.project.server.business.entities.Citizen;
import pt.ul.fc.css.project.server.business.entities.Delegate;
import pt.ul.fc.css.project.server.business.entities.NonDelegate;
import pt.ul.fc.css.project.server.business.handlers.exceptions.EmptyFieldsException;
import pt.ul.fc.css.project.server.business.repositories.interfaces.CitizenRepositoryInterface;

/** Controller for the LawProject class */
@Component
public class CitizenHandler {

	@Autowired
	private CitizenRepositoryInterface cRep;
	private Long current_citizen_id;

	public Citizen add_citizen(String name, Long id, boolean isDelegate) throws EmptyFieldsException {

		if (name.isEmpty()) {
            throw new EmptyFieldsException("Name is a required field");
        }
		Citizen c = isDelegate ? new Delegate(name) : new NonDelegate(name);
		c.setId(id);
		cRep.save(c);
		this.current_citizen_id = id;
		return c;
	}

	public Citizen getCitizenById(Long id) {
		return cRep.findById(id).get();
	}

	public Long get_current_citizen_id() {
		return this.current_citizen_id;
	}

    public void setCitizenId(Long id) {
		this.current_citizen_id = id;
    }
}
