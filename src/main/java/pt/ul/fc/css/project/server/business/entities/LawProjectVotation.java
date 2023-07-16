package pt.ul.fc.css.project.server.business.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import pt.ul.fc.css.project.server.business.entities.enums.Vote;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

/** Class that creates Projects in votation */
@Embeddable
public class LawProjectVotation {

  private LocalDateTime close_date_votation;
  private boolean isActive;

  @Transient private ScheduledExecutorService executor; // To use for the countdown

  // Counter for favorable votes
  private int favorable_vote_counter;
  // Counter for unfavorable votes
  private int unfavorable_vote_counter;

  /** Constructor for when there is no votation active */
  public LawProjectVotation() {
    this.isActive = false;
  }

  /**
   * Constructor for when there is a votation active
   *
   * @param lp the law project to which the votation will be associated with
   * @param citRep the repository of citizens
   */
  public LawProjectVotation(LawProject lp) {
    this.isActive = true;
    this.close_date_votation = lp.get_closed_date(lp.getClose_date());
    this.favorable_vote_counter = 0;
    this.unfavorable_vote_counter = 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(close_date_votation, favorable_vote_counter, unfavorable_vote_counter);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    LawProjectVotation other = (LawProjectVotation) obj;
    return Objects.equals(close_date_votation, other.close_date_votation)
        && favorable_vote_counter == other.favorable_vote_counter
        && unfavorable_vote_counter == other.unfavorable_vote_counter;
  }

  @Override
  public String toString() {
    return "LawProjectVotation [close_date="
        + close_date_votation
        + ", favorable_vote_counter="
        + favorable_vote_counter
        + ", unfavorable_vote_counter="
        + unfavorable_vote_counter
        + "]";
  }

  /**
   * Votes on the project and updates the counters of vote
   *
   * @param vote its the vote of the citizen
   */
  public void votes(Vote vote) {
    if (vote.equals(Vote.FAVOR)) favorable_vote_counter++;
    else unfavorable_vote_counter++;
  }

  public boolean getIsActive() {
    return isActive;
  }

  public LocalDateTime getClose_date_votation() {
    return close_date_votation;
  }

  public void setClose_date_votation(LocalDateTime close_date_votation) {
    this.close_date_votation = close_date_votation;
  }

  public int getFavorable_vote_counter() {
    return favorable_vote_counter;
  }

  public void setFavorable_vote_counter(int favorable_vote_counter) {
    this.favorable_vote_counter = favorable_vote_counter;
  }

  public int getUnfavorable_vote_counter() {
    return unfavorable_vote_counter;
  }

  public void setUnfavorable_vote_counter(int unfavorable_vote_counter) {
    this.unfavorable_vote_counter = unfavorable_vote_counter;
  }

  public void checkExpiration() {
    LocalDateTime time = LocalDateTime.now();
    if(this.close_date_votation.isBefore(time))
      this.isActive = false;
    else
      this.isActive = true;
  }

}
