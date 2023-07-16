package pt.ul.fc.css.project.server.business.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ul.fc.css.project.server.business.entities.Citizen;
import pt.ul.fc.css.project.server.business.entities.Delegate;
import pt.ul.fc.css.project.server.business.entities.LawProject;
import pt.ul.fc.css.project.server.business.entities.Theme;
import pt.ul.fc.css.project.server.business.handlers.CitizenHandler;
import pt.ul.fc.css.project.server.business.handlers.ManagementHandler;
import pt.ul.fc.css.project.server.business.handlers.exceptions.AlreadSignedException;
import pt.ul.fc.css.project.server.business.handlers.exceptions.CitizenNotFoundException;
import pt.ul.fc.css.project.server.business.handlers.exceptions.LawProjectNotFoundException;
import pt.ul.fc.css.project.server.business.repositories.interfaces.CitizenRepositoryInterface;
import pt.ul.fc.css.project.server.business.repositories.interfaces.LawProjectRepositoryInterface;
import pt.ul.fc.css.project.server.business.repositories.interfaces.ThemeRepositoryInterface;
import pt.ul.fc.css.project.server.business.services.dtos.CitizenDTO;
import pt.ul.fc.css.project.server.business.services.dtos.DelegateDTO;
import pt.ul.fc.css.project.server.business.services.dtos.LawProjectDTO;

/** Service class for ManagementHandler */
@Service
public class ManagementService {

	@Autowired private ManagementHandler managementHandler;

	@Autowired private CitizenRepositoryInterface cRep;

	@Autowired private LawProjectRepositoryInterface lRep;

	@Autowired private ThemeRepositoryInterface tRep;

	@Autowired private CitizenHandler citizenHandler;


	/**
	 * Get the list of law projects that are open to votation
	 *
	 * @return a list of law projects that are open to votation
	 */
	public List<LawProjectDTO> get_lawProjectVotations() {
		ArrayList<LawProjectDTO> arr = new ArrayList<>();
		for (LawProject c : lRep.findCurrentVotations()) {
			LawProjectDTO c2 = dtofy(c);
			arr.add(c2);
		}
		return arr;
	}
	public LawProjectDTO sign_lawProject(Long citizen_id, Long lawProject_id) {

		 // This is the method in the Handler that contains the business logic
		 try {
			managementHandler.sign_lawProject(lawProject_id, citizen_id);
		} catch (AlreadSignedException e) {
			throw new AlreadSignedException();
		}

		// After the law project was signed, fetch the updated state of the law project
		LawProject updatedLawProject = lRep.findById(lawProject_id)
				.orElseThrow(() -> new LawProjectNotFoundException(lawProject_id));
		return dtofy(updatedLawProject);
	}

	public void chooseDelegate(Long delegate_id, Long theme_id, Long citizen_id) {
		managementHandler.chooseDelegate(delegate_id, theme_id, citizen_id);
	}

	public boolean vote_on_a_law_project(Long citizen_id, Long lawProject_id, boolean want_to_change) {
		if(managementHandler.checkIfVotedOnAlawProject(citizen_id, lawProject_id))
			return true;
		managementHandler.vote_on_a_law_project(citizen_id, lawProject_id, want_to_change);
		return false;
	}

	/**
	 * Get the delegate of a citizen for a theme
	 *
	 * @param citizen_id the id of the citizen
	 * @param theme_id   the id of the theme
	 * @return the id of the delegate of the citizen for the theme
	 * @throws CitizenNotFoundException if the citizen with the given id doesn't
	 *                                  exist
	 */
	public Optional<CitizenDTO> getDelegate(Long citizen_id, Long theme_id) {
		Long delegateId = managementHandler.getDelegate(citizen_id, theme_id);
		return cRep.findById(delegateId).map(ManagementService::dtofy);
	}

	public List<CitizenDTO> findCitizensWhoDidNotVoteOnLawProject(Long lawProject_id) {
	    List<Citizen> citizens = cRep.findCitizensWhoDidNotVoteOnLawProject(lawProject_id);
	    List<CitizenDTO> citizenDTOs = citizens.stream()
	                                           .map(ManagementService::dtofy)
	                                           .collect(Collectors.toList());
	    return citizenDTOs;
	}


	private static CitizenDTO dtofy(Citizen c) {
		CitizenDTO c2 = new CitizenDTO();
		c2.setId(c.getId());
		c2.setName(c.getName());
		return c2;
	}

	private LawProjectDTO dtofy(LawProject lp) {
		LawProjectDTO lpDTO = new LawProjectDTO();
		lpDTO.setTitle(lp.getTitle());
		lpDTO.setDescription(lp.getDescription());
		lpDTO.setPdf_attachment(lp.getPdfAttachment());
		lpDTO.setClose_date(lp.getClose_date());
		lpDTO.setDelegate_id(lp.getDelegate_id());
		String delName = cRep.findById(lp.getDelegate_id()).map(Citizen::getName).orElse("");
		String themeName = tRep.findById(lp.getTheme_id()).map(Theme::getName).orElse("");
		lpDTO.setDelegate_name(delName);
		lpDTO.setTheme_name(themeName);
		lpDTO.setTheme_id(lp.getTheme_Id());
		lpDTO.setVotation(lp.getVotation().getIsActive());
		lpDTO.setId(lp.getId());
		lpDTO.setDelegate_vote(this.managementHandler.get_delegate_vote(this.citizenHandler.get_current_citizen_id(),lp.getId()));
		return lpDTO;
	}

	public List<Theme> getAllThemes() {
		return tRep.findAll();
	}

	public List<DelegateDTO> getDelegates() {
		List<Delegate> delegates = cRep.findDelegates();
		return delegates.stream()
						.map(this::convertToDto)
						.collect(Collectors.toList());
	}

	private DelegateDTO convertToDto(Delegate delegate) {
		DelegateDTO delegateDTO = new DelegateDTO();
		delegateDTO.setId(delegate.getId());
		delegateDTO.setName(delegate.getName());
		return delegateDTO;
	}
	public String getDelegateVote(Long citizenDTO, Long id) {
		return  this.managementHandler.get_delegate_vote(citizenDTO , id).toString();
	}
}
