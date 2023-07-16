package pt.ul.fc.css.project.server.business.entities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import pt.ul.fc.css.project.server.business.converters.LocalDateTimeConverter;
import pt.ul.fc.css.project.server.business.entities.enums.Vote;

/** The LawProject class represents a law project that can be created by a delegate. */
@Entity
public class LawProject {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @Transient private ScheduledExecutorService executor;

  @Column(columnDefinition = "TEXT")
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "pds_attachment") private String pdf_attachment;

  @Column
  @Convert(converter = LocalDateTimeConverter.class)
  private LocalDateTime close_date;

  @Column(name = "closed")
  private boolean closed;
  private int signatures_count;

  @Column(name = "delegate_id")
  private Long delegate_id;

  @Column(name = "theme_id")
  private Long theme_id;

  @Enumerated(EnumType.STRING)
  private Vote final_vote;

  @Embedded private LawProjectVotation votation;

  /** Empty constructor for the LawProject class. */
  public LawProject() {}

  /**
   * Constructor for the LawProject class.
   *
   * @param title the title of the law project
   * @param description the description of the law project
   * @param pdf_attachment the pdf attachment of the law project
   * @param close_date the close date of the law project
   * @param delegate_id the id of the delegate that created the law project
   * @param theme_id the id of the theme of the law project
   */
  public LawProject(
      String title,
      String description,
      String pdf_attachment,
      LocalDateTime close_date,
      Long delegate_id,
      Long theme_id) {

    this.title = title;
    this.description = description;
    this.pdf_attachment = pdf_attachment;
    this.close_date = checkCloseDate(close_date);
    this.closed = false;
    this.signatures_count = 0;
    this.delegate_id = delegate_id;
    this.theme_id = theme_id;
    this.final_vote = Vote.EMPTY;
    this.votation = new LawProjectVotation();
  }

  public LawProject(String string, String string2, Object object, Object object2, int i, int j) {
}

/**
   * Get the title of the law project.
   *
   * @return the title of the law project
   */
  public String getTitle() {
    return title;
  }

  /**
   * Get the description of the law project.
   *
   * @return the description of the law project
   */
  public String getDescription() {
    return description;
  }

  /**
   * Get the pdf attachment of the law project.
   *
   * @return the pdf attachment of the law project
   */
  public String getPdf_attachment() {
    return pdf_attachment;
  }

  public void setTheme_id(Long theme_id) {
    this.theme_id = theme_id;
  }

  public Long getTheme_id() {
    return this.theme_id;
  }

  /**
   * Get the close date of the law project.
   *
   * @return the close date of the law project
   */
  public LocalDateTime getClose_date() {
    return close_date;
  }

  /**
   * Check if the law project is closed.
   *
   * @return true if the law project is closed, false otherwise
   */
  public boolean isClosed() {
    return closed;
  }


  /**
   * Get the signatures count of the law project.
   *
   * @return the signatures count of the law project
   */
  public int getSignatures_count() {
    return signatures_count;
  }

  public void incrementSignatures_count() {
    this.signatures_count++;
  }

  /**
   * Get the creator's id of the law project.
   *
   * @return the creator's id of the law project
   */
  public long getDelegate_id() {
    return delegate_id;
  }

  public void setDelegate_id(Long delegate_id) {
    this.delegate_id = delegate_id;
  }

  /**
   * Get the theme's id of the law project.
   *
   * @return the theme's id of the law project
   */
  public Long getTheme_Id() {
    return theme_id;
  }

  /**
   * Get the id of the law project.
   *
   * @return the id of the law project
   */
  public Long getId() {
    return id;
  }

  /**
   * Get the final vote of the law project.
   *
   * @return the final vote of the law project
   */
  public Vote getFinal_vote() {
    return final_vote;
  }

  /**
   * Get the votation of the law project.
   *
   * @return the votation of the law project
   */
  public LawProjectVotation getVotation() {
    return votation;
  }

  public void setVotation() {
    this.votation = new LawProjectVotation(this);
  }

  /** ONLY FOR TESTING PURPOSES. Set the signatures count of the law project to 10000. */
  public void setSign() {
    this.signatures_count = 10000;
    this.votation = new LawProjectVotation(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, id, title);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    LawProject other = (LawProject) obj;
    return Objects.equals(description, other.description)
        && Objects.equals(id, other.id)
        && Objects.equals(title, other.title);
  }

  @Override
  public String toString() {
    return "LawProject [id="
        + id
        + ", title="
        + title
        + ", description="
        + description
        + ", pdf_attachment="
        + pdf_attachment
        + ", closeDate="
        + close_date
        + ", closed="
        + closed
        + ", signatures_count="
        + signatures_count
        + "]";
  }

  /**
   * Returns the date accordingly with the close_date of the project when its added to the votation
   *
   * @param close_date its the close date of the project
   * @return the new date for closure
   */
  public LocalDateTime get_closed_date(LocalDateTime close_date) {
    LocalDateTime time = LocalDateTime.now();
    Duration duration = Duration.between(time, close_date);
    long daysUntilClosedDate = duration.toDays();

    if (daysUntilClosedDate < 15) return time.plusDays(16);
    else if (daysUntilClosedDate > 60.83) return time.plusDays(62);
    else return time.plusDays(daysUntilClosedDate);
  }

  /**
   * Returns the date in case a project has more than 1 year of duration
   *
   * @param close_date its the close date of the project
   * @return the new close date for the project
   */
  private LocalDateTime checkCloseDate(LocalDateTime close_date) {
    LocalDateTime time = LocalDateTime.now();
    Duration duration = Duration.between(time, close_date);
    Long days_until_close_date = duration.toDays();
    if (days_until_close_date > 365) return time.plusDays(365);
    else return close_date;
  }

  /** Used for testing purposes only */
  public void setClosedDate(LocalDateTime now) {
    this.close_date = now;
  }

  public Long getDelegate() {
    return delegate_id;
  }

  public Long getTheme() {
    return theme_id;
  }

  public void checkExpiration() {
    LocalDateTime time = LocalDateTime.now();
    if(this.close_date.isBefore(time))
      this.closed = true;
    else
      this.closed = false;
  }

  public void setfinalVote(boolean final_vote) {
    this.final_vote = final_vote ? Vote.FAVOR : Vote.AGAINST;
  }

  public String getPdfAttachment() {
    return this.pdf_attachment;
  }

  public void setClosed(boolean closed) {
    this.closed = closed;
  }

}
