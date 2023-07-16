package pt.ul.fc.css.project.server.business.handlers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pt.ul.fc.css.project.server.business.entities.Citizen;
import pt.ul.fc.css.project.server.business.entities.Delegate;
import pt.ul.fc.css.project.server.business.entities.LawProject;
import pt.ul.fc.css.project.server.business.entities.enums.Vote;
import pt.ul.fc.css.project.server.business.entities.exceptions.DurationCantBeNegException;
import pt.ul.fc.css.project.server.business.handlers.exceptions.CitizenIsNotDelegateException;
import pt.ul.fc.css.project.server.business.handlers.exceptions.CitizenNotFoundException;
import pt.ul.fc.css.project.server.business.handlers.exceptions.DelegateNotFoundException;
import pt.ul.fc.css.project.server.business.handlers.exceptions.LawProjectNotFoundException;
import pt.ul.fc.css.project.server.business.handlers.exceptions.ThemeNotFoundException;
import pt.ul.fc.css.project.server.business.repositories.interfaces.CitizenRepositoryInterface;
import pt.ul.fc.css.project.server.business.repositories.interfaces.LawProjectRepositoryInterface;
import pt.ul.fc.css.project.server.business.repositories.interfaces.ThemeRepositoryInterface;

/** Controller for the LawProject class */
@Component
public class LawProjectHandler {

	@Autowired
	// Repository for themes
	private ThemeRepositoryInterface tRep;
	@Autowired
	// Repository for citizens
	private CitizenRepositoryInterface cRep;
	@Autowired
	// Repository for law projects
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
	public LawProject add_law_project(
			String title,
			String description,
			String pdf_attachment,
			LocalDateTime closeDate,
			Long delegate_id,
			Long theme_id) {

		if (closeDate.isBefore(LocalDateTime.now())) throw new DurationCantBeNegException();

		// Find the citizen with the given id and throw an exception if it doesn't exist
		Citizen delegate =
				cRep.findById(delegate_id).orElseThrow(() -> new CitizenNotFoundException(delegate_id));
		if (!tRep.findById(theme_id).isPresent()) throw new ThemeNotFoundException();
		// Check if the citizen is a delegate and create the law project, if not throw
		// an exception
		if (delegate instanceof Delegate) {
			LawProject lawProject =
					((Delegate) delegate)
					.create_law_project(title, description, pdf_attachment, closeDate, theme_id);

			lRep.save(lawProject);
			return lawProject;
		} else throw new CitizenIsNotDelegateException(delegate_id);
	}

	@Scheduled(fixedRate = 5000)
	public void closeLawProject() {

		List<LawProject> lawprojects = lRep.findAll();
		lawprojects.forEach(lawproject -> lawproject.checkExpiration());
		lRep.saveAll(lawprojects);
	}

	@Scheduled(fixedRate = 5000)
	public void closeLawProjectVotation() {
		List<LawProject> lawprojects = lRep.findCurrentVotations();
		lawprojects.forEach(lawproject -> lawproject.getVotation().checkExpiration());
		lawprojects.forEach(lawproject -> close_lawProjectVotation(lawproject));
		lRep.saveAll(lawprojects);
	}


	/**
	 * Closes the votation of the project
	 *
	 * @param citizens the list of citizens who did not vote on the project
	 * @param citRep the repository of citizens
	 */
	private void close_lawProjectVotation(LawProject lawProject) {

		List<Citizen> citizens = cRep.findCitizensWhoDidNotVoteOnLawProject(lawProject.getId());
		int favorable_vote_counter = lawProject.getVotation().getFavorable_vote_counter();
		int unfavorable_vote_counter = lawProject.getVotation().getUnfavorable_vote_counter();

		favorable_vote_counter +=
			citizens.stream()
				.filter(citizen -> Vote.FAVOR.equals(this.getDelegateVote(lawProject, citizen, cRep)))
				.count();

		unfavorable_vote_counter +=
			citizens.stream()
				.filter(citizen -> Vote.AGAINST.equals(this.getDelegateVote(lawProject, citizen, cRep)))
				.count();

		updateLawProjectStatus(favorable_vote_counter, unfavorable_vote_counter, lawProject);
	}

	/**
	 * Gets the vote of the delegate associated with the citizen for the theme of this law project
	 * @param lawProject
	 *
	 * @param citizen the citizen
	 * @param citRep the repository of citizens
	 * @return the Vote of the delegate associated with the citizen for the theme of this law project
	 */
	private Vote getDelegateVote(LawProject lawProject, Citizen citizen, CitizenRepositoryInterface citRep) {

		if (citizen instanceof Delegate && lawProject.getDelegate_id() == citizen.getId())
			return ((Delegate) citizen).getDelegateVote(lawProject.getId());

		Long delegate_id = citizen.getDelegate(lawProject.getTheme_Id());
		if (delegate_id == -1L) { // citizen has no delegate associated with that theme
			return Vote.EMPTY;
		}
		Delegate delegate =
			(Delegate)
				citRep
					.findById(delegate_id)
					.orElseThrow(() -> new DelegateNotFoundException());
		return delegate.getDelegateVote(lawProject.getId());
	}

	/** Closes the law project */
	private void updateLawProjectStatus(int favorable_vote_counter, int unfavorable_vote_counter, LawProject lawproject) {

		if (favorable_vote_counter > (favorable_vote_counter + unfavorable_vote_counter) / 2)
			lawproject.setfinalVote(true);
		else
			lawproject.setfinalVote(false);
	}

	/**
	 * ONLY FOR TESTING - See all the law projects
	 *
	 * @return a list with all the law projects
	 */
	public List<LawProject> get_lawProjects() {
		return lRep.findAll();
	}

	/**
	 * ONLY FOR TESTING - See all the law projects that are not expired
	 *
	 * @return a list with all the law projects that are not expired
	 */
	public List<LawProject> findNonExpiredProjects() {
		return lRep.findNonExpiredProjects();
	}

	/**
	 * ONLY FOR TESTING - See all the law projects that are expired
	 *
	 * @return a list with all the law projects that are expired
	 */
	public List<LawProject> findExpiredProjects() {
		return lRep.findExpiredProjects();
	}

	/**
	 * ONLY FOR TESTING - See all the law projects that are closed
	 *
	 * @return a list with all the law projects that are closed
	 */
	public List<LawProject> findClosedVotations() {
		return lRep.findClosedVotations();
	}

	/**
	 * ONLY FOR TESTING - Find a law project by id
	 *
	 * @param id the id of the law project
	 * @return the law project with the given id
	 */
	public Optional<LawProject> findById(Long id) {
		return lRep.findById(id);
	}

	/**
	 * ONLY FOR TESTING - Set signatures of lp to 10000
	 *
	 * @param lp the law project
	 */
	public LawProject set_sign(Long lawProject_id){
	// Find the law project with the given id and throw an exception if it doesn't exist
	LawProject lp =
	lRep.findById(lawProject_id)
		.orElseThrow(() -> new LawProjectNotFoundException(lawProject_id));
	// Set the law project signatures_count to 10 000
	lp.setSign();
	// Save the updated law project in the repository
	lRep.save(lp);
	return lp;
	}

	public LawProject force_close(Long lawProject_id) {

		LawProject lp =
		lRep.findById(lawProject_id)
			.orElseThrow(() -> new LawProjectNotFoundException(lawProject_id));

		lp.setClosed(true);
		lRep.save(lp);
		return lp;
	}
}
