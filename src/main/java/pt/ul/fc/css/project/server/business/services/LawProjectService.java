package pt.ul.fc.css.project.server.business.services;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pt.ul.fc.css.project.server.business.entities.Citizen;
import pt.ul.fc.css.project.server.business.entities.LawProject;
import pt.ul.fc.css.project.server.business.entities.Theme;
import pt.ul.fc.css.project.server.business.handlers.LawProjectHandler;
import pt.ul.fc.css.project.server.business.handlers.exceptions.CitizenIsNotDelegateException;
import pt.ul.fc.css.project.server.business.handlers.exceptions.CitizenNotFoundException;
import pt.ul.fc.css.project.server.business.repositories.interfaces.CitizenRepositoryInterface;
import pt.ul.fc.css.project.server.business.repositories.interfaces.LawProjectRepositoryInterface;
import pt.ul.fc.css.project.server.business.repositories.interfaces.ThemeRepositoryInterface;
import pt.ul.fc.css.project.server.business.services.dtos.LawProjectDTO;

/** Service class for LawProjectController */
@Component
public class LawProjectService {

	@Autowired
	private LawProjectHandler lawProjectHandler;
	@Autowired
	private ThemeRepositoryInterface tRep;
	@Autowired
	private CitizenRepositoryInterface cRep;
	@Autowired
	private LawProjectRepositoryInterface lRep;
	/**
	 * Add a law project
	 *
	 * @param title the title of the law project
	 * @param description the description of the law project
	 * @param pdf_attachment the pdf attachment of the law project
	 * @param closeDate the close date of the law project
	 * @param delegate_id the id of the delegate
	 * @param theme_id the id of the theme
	 * @throws CitizenNotFoundException if the citizen with the given id doesn't exist
	 * @throws CitizenIsNotDelegateException if the citizen with the given id is not a delegate
	 */
	public LawProjectDTO add_law_project(
			String title,
			String description,
			String pdf_attachment,
			LocalDateTime closeDate,
			Long delegate_id,
			Long theme_id) {

		LawProject lp = lawProjectHandler.add_law_project(title, description, pdf_attachment, closeDate, delegate_id, theme_id);
		return dtofy(lp);
	}

	/**
	 * ONLY FOR TESTING - See all the law projects
	 *
	 * @return a list with all the law projects
	 */
	public List<LawProjectDTO> get_lawProjects() {
		ArrayList<LawProjectDTO> arr = new ArrayList<>();
		for (LawProject lp : lRep.findAll()) {
			LawProjectDTO lp2 = dtofy(lp);
			arr.add(lp2);
		}
		return arr;
	}

	/**
	 * ONLY FOR TESTING - See all the law projects that are not expired
	 *
	 * @return a list with all the law projects that are not expired
	 */
	public List<LawProjectDTO> findNonExpiredProjects() {
		ArrayList<LawProjectDTO> arr = new ArrayList<>();
		for (LawProject lp : lRep.findNonExpiredProjects()) {
			LawProjectDTO lp2 = dtofy(lp);
			arr.add(lp2);
		}
		return arr;
	}

	/**
	 * ONLY FOR TESTING - See all the law projects that are expired
	 *
	 * @return a list with all the law projects that are expired
	 */
	public List<LawProjectDTO> findExpiredProjects() {
		ArrayList<LawProjectDTO> arr = new ArrayList<>();
		for (LawProject lp : lRep.findExpiredProjects()) {
			LawProjectDTO lp2 = dtofy(lp);
			arr.add(lp2);
		}
		return arr;
	}

	/**
	 * ONLY FOR TESTING - See all the law projects that are closed
	 *
	 * @return a list with all the law projects that are closed
	 */
	public List<LawProjectDTO> findClosedVotations() {
		ArrayList<LawProjectDTO> arr = new ArrayList<>();
		for (LawProject lp : lRep.findClosedVotations()) {
			LawProjectDTO lp2 = dtofy(lp);
			arr.add(lp2);
		}
		return arr;
	}

	public Optional<LawProjectDTO> getLawProject(Long id) {
        return lRep.findById(id).map(this::dtofy);
	}

	/**
	 * ONLY FOR TESTING - Find a law project by id
	 *
	 * @param id the id of the law project
	 * @return the law project with the given id
	 */
	public Optional<LawProjectDTO> findById(Long id) {
        return lRep.findById(id).map(this::dtofy);
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
		return lpDTO;
	}

	/**
	 * ONLY FOR TESTING - Set signatures of lp to 10000
	 *
	 * @param lp the law project
	 */
	public LawProjectDTO set_sign(Long lawProject_id){
		return dtofy(lawProjectHandler.set_sign(lawProject_id));
	}

	/**
	 * ONLY FOR TESTING - Set the expiration date
	 *
	 * @param lp the law project
	 */
	public LawProjectDTO force_close(Long lawProject_id){
		return dtofy(lawProjectHandler.force_close(lawProject_id));
	}
}
