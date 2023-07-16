package pt.ul.fc.css.project.server.business.services.dtos;

public class DelegateDTO extends CitizenDTO {

  private Long id;
  private String name;

  public DelegateDTO() {
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

}
