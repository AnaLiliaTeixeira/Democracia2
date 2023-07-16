package pt.ul.fc.css.project.server.business.services.dtos;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.springframework.stereotype.Component;

import pt.ul.fc.css.project.server.business.entities.enums.Vote;

@Component
public class LawProjectDTO {
  private LocalDateTime close_date;
  private String description;
  private String title;
  private Long delegate_id;
  private Long theme_id;
  private boolean votation;
  private String pdf_attachment;
  private String delegate_name;
  private String theme_name;
  private Vote delegate_vote;
  private Long id;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDateTime getClose_date() {
    return close_date;
  }

  public void setClose_date(LocalDateTime close_date) {
    this.close_date = close_date;
  }

  public Long getDelegate_id() {
    return delegate_id;
  }

  public void setDelegate_id(Long delegate_id) {
    this.delegate_id = delegate_id;
  }

  public Long getTheme_id() {
    return theme_id;
  }

  public void setTheme_id(Long theme_id) {
    this.theme_id = theme_id;
  }

  public boolean getVotation() {
    return votation;
  }

  public String getTitle() {
    return title;
  }

  public void setVotation(boolean vote) {
    this.votation = vote;
  }

  public String getDescription() {
    return this.description;
  }

  public String getPdf_attachment() {
    return this.pdf_attachment;
  }

  public void setDelegate_name(String delName) {
    this.delegate_name = delName;
  }

  public void setTheme_name(String themeName) {
    this.theme_name = themeName;
  }

  public String getTheme_name() {
    return this.theme_name;
  }

  public String getDelegate_name() {
    return this.delegate_name;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setPdf_attachment(String pdfAttachment) {
    this.pdf_attachment = pdfAttachment;
  }

  public void setDelegate_vote(Vote get_delegate_vote) {
    this.delegate_vote = get_delegate_vote;
  }

  public Vote getDelegate_vote() {
    return this.delegate_vote;
  }

  @Override
  public String toString() {
    return "LawProjectDTO [close_date=" + close_date + ", description=" + description + ", title=" + title
        + ", delegate_id=" + delegate_id + ", theme_id=" + theme_id + ", votation=" + votation + ", pdf_attachment="
        + pdf_attachment + ", delegate_name=" + delegate_name + ", theme_name=" + theme_name + ", delegate_vote="
        + delegate_vote + ", id=" + id + "]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    LawProjectDTO that = (LawProjectDTO) o;
    return id == that.id &&
        Objects.equals(title, that.title) &&
        Objects.equals(description, that.description) &&
        Objects.equals(pdf_attachment, that.pdf_attachment) &&
        Objects.equals(close_date.truncatedTo(ChronoUnit.SECONDS), that.close_date.truncatedTo(ChronoUnit.SECONDS)) &&
        Objects.equals(theme_id, that.theme_id) &&
        Objects.equals(delegate_id, that.delegate_id) &&
        Objects.equals(votation, that.votation) &&
        Objects.equals(delegate_name, that.delegate_name) &&
        Objects.equals(theme_name, that.theme_name) &&
        Objects.equals(delegate_vote, that.delegate_vote);
  }
}
