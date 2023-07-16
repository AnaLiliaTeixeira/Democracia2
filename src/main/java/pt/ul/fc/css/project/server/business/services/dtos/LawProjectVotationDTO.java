package pt.ul.fc.css.project.server.business.services.dtos;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class LawProjectVotationDTO {

  private LocalDateTime close_date_votation;
  private boolean isActive;

  public LocalDateTime getClose_date_votation() {
    return close_date_votation;
  }

  public void setClose_date_votation(LocalDateTime close_date_votation) {
    this.close_date_votation = close_date_votation;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }
}
