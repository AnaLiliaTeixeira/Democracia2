package pt.ul.fc.css.project.server.business.handlers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pt.ul.fc.css.project.server.business.entities.Citizen;
import pt.ul.fc.css.project.server.business.entities.Delegate;
import pt.ul.fc.css.project.server.business.entities.LawProject;
import pt.ul.fc.css.project.server.business.entities.NonDelegate;
import pt.ul.fc.css.project.server.business.entities.Theme;
import pt.ul.fc.css.project.server.business.entities.enums.Vote;
import pt.ul.fc.css.project.server.business.handlers.exceptions.AlreadSignedException;
import pt.ul.fc.css.project.server.business.handlers.exceptions.CitizenNotFoundException;
import pt.ul.fc.css.project.server.business.handlers.exceptions.DelegateNotFoundException;
import pt.ul.fc.css.project.server.business.handlers.exceptions.LawProjectClosedException;
import pt.ul.fc.css.project.server.business.handlers.exceptions.LawProjectNotFoundException;
import pt.ul.fc.css.project.server.business.handlers.exceptions.ThemeNotFoundException;
import pt.ul.fc.css.project.server.business.repositories.interfaces.CitizenRepositoryInterface;
import pt.ul.fc.css.project.server.business.repositories.interfaces.LawProjectRepositoryInterface;
import pt.ul.fc.css.project.server.business.repositories.interfaces.ThemeRepositoryInterface;

/** Controller for the LawProject class and the Citizen class */
@Component
public class ManagementHandler {

  @Autowired
  // Repository for citizens
  private CitizenRepositoryInterface cRep;
  @Autowired
  // Repository for law projects
  private LawProjectRepositoryInterface lRep;
  @Autowired
  // Repository for themes
  private ThemeRepositoryInterface tRep;


  /**
   * Sign a law project
   *
   * @param lawProject_id the id of the law project
   * @param citizen_id    the id of the citizen that wants to sign the law project
   * @throws CitizenNotFoundException    if the citizen with the given id doesn't
   *                                     exist
   * @throws LawProjectNotFoundException if the law project with the given id
   *                                     doesn't exist
   * @throws DelegateNotFoundException   if the delegate with the given id doesn't
   *                                     exist
   */
  public void sign_lawProject(Long lawProject_id, Long citizen_id) {

    if(hasSigned(citizen_id, lawProject_id))
      throw new AlreadSignedException();

    // Find the citizen with the given id and throw an exception if it doesn't exist
    Citizen citizen = cRep.findById(citizen_id).orElseThrow(() -> new CitizenNotFoundException(citizen_id));

    // Find the law project with the given id and throw an exception if it doesn't
    // exist
    LawProject lp = lRep.findById(lawProject_id)
        .orElseThrow(() -> new LawProjectNotFoundException(lawProject_id));
    // Add the law project to the citizen's list of signed law projects
    // Increase the number of signatures_count of the law project
    if (!lp.isClosed()) {
      lp.incrementSignatures_count();
      lRep.save(lp);

      if (lp.getSignatures_count() >= 10000) {
        lp.setClosedDate(lp.get_closed_date(lp.getClose_date()));
        lRep.save(lp);
        lp.setVotation();
        lRep.save(lp);
        // Get the id of the law project creator
        long delegate_id = lp.getDelegate_id();
        // Find the delegate with the given id and throw an exception if it doesn't
        // exist
        Delegate delegate = (Delegate) cRep.findById(delegate_id)
            .orElseThrow(() -> new DelegateNotFoundException());
        // Creator's automatic vote in favor of the law project
        delegate.votes(lp.getId(), Vote.FAVOR);
        lp.getVotation().votes(Vote.FAVOR);
        // Save the updated delegate in the repository
        cRep.save(delegate);
      }
      citizen.add_law_project_sign(lp.getId());
    } else
      throw new LawProjectClosedException(lp.getId());

    // Save the citizen in the repository
    cRep.save(citizen);
    // Save the updated law project in the repository
    lRep.save(lp);
  }


  /**
   * Choose a delegate for a theme
   *
   * @param delegate_id the id of the delegate
   * @param theme_id    the id of the theme
   * @param citizen_id  the id of the citizen that wants to choose the delegate
   * @throws CitizenNotFoundException if the citizen with the given id doesn't
   *                                  exist
   */
  public void chooseDelegate(Long delegate_id, Long theme_id, Long citizen_id) {

    // Find the citizen with the given id and throw an exception if it doesn't exist
    Citizen citizen = cRep.findById(citizen_id).orElseThrow(() -> new CitizenNotFoundException(citizen_id));

    // Add the delegate to the citizen's list of delegates for the given theme
    citizen.add_del_to_theme(theme_id, delegate_id);
    // Save the updated citizen in the repository
    cRep.save(citizen);
  }

  /**
   * Get the list of law projects that are open to votation
   *
   * @return a list of law projects that are open to votation
   */
  public List<LawProject> get_lawProjectVotations() {
    return lRep.findCurrentVotations();
  }

  /**
   * Updates a citizen's vote on a law project, potentially changing the
   * delegate's vote.
   *
   * @param citizen_id    the ID of the citizen who wants to change their vote
   * @param lawProject_id the ID of the law project for which the vote is being
   *                      changed
   * @param delegate_vote the current vote of the citizen's delegate
   * @param decision      the decision of the citizen: true if they want to change
   *                      the delegate's vote, false otherwise
   * @throws CitizenNotFoundException    if the citizen with the given ID doesn't
   *                                     exist
   * @throws LawProjectNotFoundException if the law project with the given ID
   *                                     doesn't exist
   * @throws LawProjectClosedException   if the law project with the given ID is closed
   */
  public void vote_on_a_law_project(Long citizen_id, Long lawProject_id, boolean want_to_change) {
    // Find the law project with the given ID and throw an exception if it doesn't
    // exist
    LawProject lawProject = lRep.findById(lawProject_id)
        .orElseThrow(() -> new LawProjectNotFoundException(lawProject_id));
    if (lawProject.isClosed()) {
      throw new LawProjectClosedException(lawProject_id);
    }

    Vote vote = want_to_change ? Vote.AGAINST : Vote.FAVOR;
    // Find the citizen with the given ID and throw an exception if it doesn't exist
    Citizen citizen = cRep.findById(citizen_id).orElseThrow(() -> new CitizenNotFoundException(citizen_id));

    // Update the citizen's vote and the votation of the law project
    if (citizen instanceof Delegate)
      ((Delegate) citizen).votes(lawProject.getId(), vote);
    else
      ((NonDelegate) citizen).votes(lawProject.getId());

    lawProject.getVotation().votes(vote);

    // Save the updated citizen and law project in the repository
    cRep.save(citizen);
    lRep.save(lawProject);
  }

  /**
   * Get the most general vote of the citizen's delegates for a theme of a law
   * project
   *
   * @param citizen_id    the id of the citizen that wants to vote on the law
   *                      project
   * @param lawProject_id the id of the law project that the citizen wants to vote
   *                      on
   * @return the most general vote of the citizen's delegates for a theme of a law
   *         project
   * @throws CitizenNotFoundException    if the citizen with the given id doesn't
   *                                     exist
   * @throws LawProjectNotFoundException if the law project with the given id
   *                                     doesn't exist
   * @throws DelegateNotFoundException   if the delegate with the given id doesn't
   *                                     exist
   * @throws ThemeNotFoundException      if the theme with the given id doesn't
   *                                     exist
   */
  public Vote get_delegate_vote(Long citizen_id, Long lawProject_id) {
    // Find the citizen with the given ID and throw an exception if it doesn't exist
    Citizen citizen = cRep.findById(citizen_id).orElseThrow(() -> new CitizenNotFoundException(citizen_id));

    // If the citizen hasn't already voted on the law project
    if (!citizen.alreadyVoted(lawProject_id)) {

      // Find the law project with the given id and throw an exception if it doesn't
      // exist
      LawProject lp = lRep.findById(lawProject_id)
          .orElseThrow(() -> new LawProjectNotFoundException(lawProject_id));

      // Get the id of the theme of the law project
      Long theme_id = lp.getTheme_Id();
      Theme theme = tRep.findById(theme_id).orElseThrow(() -> new ThemeNotFoundException());
       // Get the id of the delegate that the citizen has chosen for the theme of the
        // law project
      Long delegate_id = citizen.getDelegate(theme_id);
      Delegate delegate = null;

      if (delegate_id != -1L)
        delegate = (Delegate) cRep.findById(delegate_id).orElseThrow(() -> new DelegateNotFoundException());

      // Find the delegate that the citizen has chosen for the theme of the law
      // project
      while (theme.getParent() != null && !citizen.hasDelegate(theme_id)) {
        theme = theme.getParent();
        theme_id = theme.getId();
        delegate_id = citizen.getDelegate(theme_id);
        // Find the delegate with the given id and throw an exception if it doesn't
        // exist
        if (delegate_id != -1L)
          delegate = (Delegate) cRep.findById(delegate_id)
            .orElseThrow(() -> new DelegateNotFoundException());
      }

      if(delegate != null)
        return delegate.getDelegateVote(lawProject_id);
    }
    // If the citizen has already voted on the law project then return Vote.EMPTY
    return Vote.EMPTY;
  }

  /**
   * Save a citizen in the repository
   *
   * @param citizen the citizen that has to be saved in the repository
   */
  public void save(Citizen citizen) {
    cRep.save(citizen);
  }

  /**
   * Save a theme in the repository
   *
   * @param theme the theme that has to be saved in the repository
   */
  public void save(Theme theme) {
    tRep.save(theme);
  }

  /**
   * Find the citizens that have not voted on a law project
   *
   * @param lawProject_id the id of the law project that the citizens have not
   *                      voted on
   * @return a list of citizens that have not voted on the law project
   */
  public List<Citizen> findCitizensWhoDidNotVoteOnLawProject(Long lawProject_id) {
    return cRep.findCitizensWhoDidNotVoteOnLawProject(lawProject_id);
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
  public Long getDelegate(Long citizen_id, Long theme_id) {
    // Find the citizen with the given id and throw an exception if it doesn't exist
    Citizen citizen = cRep.findById(citizen_id).orElseThrow(() -> new CitizenNotFoundException(citizen_id));

    return citizen.getDelegate(theme_id);
  }

  public boolean checkIfVotedOnAlawProject(Long citizen_id, Long lawProject_id) {
    Citizen citizen = cRep.findById(citizen_id).orElseThrow(() -> new CitizenNotFoundException(citizen_id));
    return citizen.alreadyVoted(lawProject_id);
  }

  public boolean hasSigned(Long citizen_id, Long lawProject_id) {
    Citizen citizen = cRep.findById(citizen_id).orElseThrow(() -> new CitizenNotFoundException(citizen_id));
    return citizen.alreadySigned(lawProject_id);
  }
}
