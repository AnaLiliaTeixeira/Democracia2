package pt.ul.fc.css.project.server.business.services.dtos;

import org.springframework.stereotype.Component;

@Component
public class CitizenDTO {

  private Long id;
  private String name;
  private boolean isDelegate = false;

  public CitizenDTO() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setIsDelegate(boolean isDelegate) {
    this.isDelegate = isDelegate;
  }

  public boolean getIsDelegate() {
      return this.isDelegate;
  }

}
