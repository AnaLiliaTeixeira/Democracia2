package pt.ul.fc.css.project.server.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pt.ul.fc.css.project.server.business.entities.LawProject;
import pt.ul.fc.css.project.server.business.entities.Theme;
import pt.ul.fc.css.project.server.business.services.CitizenService;
import pt.ul.fc.css.project.server.business.services.LawProjectService;
import pt.ul.fc.css.project.server.business.services.ManagementService;
import pt.ul.fc.css.project.server.business.services.dtos.CitizenDTO;
import pt.ul.fc.css.project.server.business.services.dtos.DelegateDTO;
import pt.ul.fc.css.project.server.business.services.dtos.LawProjectDTO;

@Controller
public class WebController {

  @Autowired
  private CitizenService citizenService;

  @Autowired
  private LawProjectService lawProjectService;

  @Autowired
  private ManagementService managementService;

  @GetMapping("/")
	public String redirectToLogin() {
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

  @GetMapping("/lawproject_list")
  public String getLawProjectList(Model model) {
    List<LawProjectDTO> lawProjects = lawProjectService.findNonExpiredProjects();
    model.addAttribute("lawprojects", lawProjects);
    return "lawproject_list";
  }


  @GetMapping("/lawprojectvotation_list")
  public String getLawProjectVotationList(Model model) {
    List<LawProjectDTO> lawProjectsVotation = managementService.get_lawProjectVotations();
    model.addAttribute("lawprojectsvotation", lawProjectsVotation);
    return "lawprojectvotation_list";
  }

  @PostMapping("/lawprojects/new")
  public String addLawProject(final Model model, @ModelAttribute LawProjectDTO lawproject, @RequestParam String close_date)  {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    LocalDateTime parsedCloseDate = LocalDateTime.parse(close_date, formatter);

    lawProjectService.add_law_project(lawproject.getTitle(), lawproject.getDescription(), lawproject.getPdf_attachment(), parsedCloseDate, citizenService.get_current_citizen_id(), lawproject.getTheme_id());
    return "redirect:/lawproject_list";
  }

  @GetMapping("/lawproject_add")
  public String newLawProject(final Model model) {
    boolean isDelegate = citizenService.isDelegate();
    model.addAttribute("isDelegate", isDelegate);
    model.addAttribute("lawproject", new LawProject());
    List<Theme> themes = managementService.getAllThemes();
    model.addAttribute("themes", themes);
    return "lawproject_add";
  }

  @GetMapping("/lawprojects/{id}")
  public String getLawProject(@PathVariable("id") Long id, Model model) {
      Optional<LawProjectDTO> lawProject = lawProjectService.getLawProject(id);
      model.addAttribute("lawproject", lawProject);
      return "lawproject_details";
  }


  @GetMapping("/error")
  public String handleError() {
    return "error";
  }

  @PostMapping("/citizen_add")
  public String addCitizen(final Model model, @ModelAttribute CitizenDTO citizen)  {
    Long citizenId = citizen.getId();
    boolean citizenExists = citizenService.checkCitizenExistsById(citizenId);

    if (!citizenExists)
        this.citizenService.add_citizen(citizen.getName(), citizenId, citizen.getIsDelegate());
    else
        this.citizenService.set_current_citizen_id(citizenId);

    try {
      return "redirect:/layout";
    } catch (Exception e) {
      return "templates/error/404";
    }
  }
  @GetMapping("/lawproject_details/{id}")
  public String getLawProjectDetails(@PathVariable Long id, Model model) {
      Optional<LawProjectDTO> lawProject = lawProjectService.getLawProject(id);
      if (lawProject.isPresent()) {
          model.addAttribute("lawproject", lawProject.get());
          return "lawproject_details";
      } else {
          return "error";
      }
  }

  @GetMapping("/votation_list")
  public String getLawProjectVotations(Model model) {
    List<LawProjectDTO> lawProjectVotations = managementService.get_lawProjectVotations();
    model.addAttribute("lawprojectvotations", lawProjectVotations);
    return "votation_list";
  }

  @GetMapping("/choose_delegate")
  public String showChooseDelegatePage(Model model) {
      List<DelegateDTO> delegates = managementService.getDelegates();
      delegates.removeIf(delegate -> delegate.getId().equals(citizenService.get_current_citizen_id()));
      List<Theme> themes = managementService.getAllThemes();

      model.addAttribute("delegates", delegates);
      model.addAttribute("themes", themes);

      return "choose_delegate";
  }
}
