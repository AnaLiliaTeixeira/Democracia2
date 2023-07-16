package pt.ul.fc.css.project.server.business.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ul.fc.css.project.server.business.entities.Citizen;
import pt.ul.fc.css.project.server.business.entities.Delegate;
import pt.ul.fc.css.project.server.business.handlers.CitizenHandler;
import pt.ul.fc.css.project.server.business.handlers.exceptions.EmptyFieldsException;
import pt.ul.fc.css.project.server.business.repositories.interfaces.CitizenRepositoryInterface;
import pt.ul.fc.css.project.server.business.services.dtos.CitizenDTO;

/** Service class for LawProjectController */
@Service
public class CitizenService {

	@Autowired
	private CitizenHandler citizenHandler;

	@Autowired
	private CitizenRepositoryInterface cRep;

	public CitizenDTO add_citizen(String name, Long id, boolean isDelegate) throws EmptyFieldsException {
		Citizen c = citizenHandler.add_citizen(name, id, isDelegate);
		return CitizenService.dtofy(c, isDelegate);
	}

	private static CitizenDTO dtofy(Citizen c, boolean isDelegate) {
		CitizenDTO cdto =new CitizenDTO();

		if(isDelegate)
			cdto.setIsDelegate(c instanceof Delegate);

		if (c != null)
			cdto.setName(c.getName());
		return cdto;
	}

    public boolean checkCitizenExistsById(Long citizenId) {
        return this.cRep.existsById(citizenId);
    }

	public CitizenDTO getCitizenById(Long citizenId) {
		Citizen cit = this.citizenHandler.getCitizenById(citizenId);
		boolean isDelegate = cit instanceof Delegate;
		return dtofy(cit, isDelegate);
	}

	public boolean isDelegate() {
		return this.cRep.findById(this.citizenHandler.get_current_citizen_id()).get() instanceof Delegate;
	}

    public boolean hasVoted(Long id) {
        return this.cRep.findById(this.citizenHandler.get_current_citizen_id()).get().alreadyVoted(id);
    }

	public void set_current_citizen_id(Long citizenId) {
		citizenHandler.setCitizenId(citizenId);
	}

    public Long get_current_citizen_id() {
        return this.citizenHandler.get_current_citizen_id();
    }

}
