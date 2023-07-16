package pt.ul.fc.css.project.server.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pt.ul.fc.css.project.server.business.handlers.exceptions.AlreadSignedException;
import pt.ul.fc.css.project.server.business.handlers.exceptions.CitizenNotFoundException;
import pt.ul.fc.css.project.server.business.handlers.exceptions.DelegateNotFoundException;
import pt.ul.fc.css.project.server.business.handlers.exceptions.LawProjectClosedException;
import pt.ul.fc.css.project.server.business.handlers.exceptions.LawProjectNotFoundException;
import pt.ul.fc.css.project.server.business.services.CitizenService;
import pt.ul.fc.css.project.server.business.services.LawProjectService;
import pt.ul.fc.css.project.server.business.services.ManagementService;
import pt.ul.fc.css.project.server.business.services.dtos.CitizenDTO;
import pt.ul.fc.css.project.server.business.services.dtos.LawProjectDTO;

@RestController()
@RequestMapping("api")
public class RestLawProjectController {

  @Autowired
  private CitizenService citizenService;

  @Autowired
  private LawProjectService lawProjectService;

  @Autowired
  private ManagementService managementService;

  @GetMapping("/lawprojects_votation")
  public ResponseEntity<List<LawProjectDTO>> getAllLawProjectsVotation() {
    List<LawProjectDTO> lawprojects = managementService.get_lawProjectVotations();
    return ResponseEntity.ok().body(lawprojects);
  }

  @GetMapping("/lawprojects/{lawProject_id}")
  public ResponseEntity<LawProjectDTO> getLawProject(@PathVariable("lawProject_id") Long id) {
    Optional<LawProjectDTO> lawProject = lawProjectService.getLawProject(id);
    return lawProject.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/lawprojects/nonexpired")
  public ResponseEntity<List<LawProjectDTO>> getAllNonExpiredLawProjects() {
    List<LawProjectDTO> lawprojects = lawProjectService.findNonExpiredProjects();
    return ResponseEntity.ok().body(lawprojects);
  }

  // Vote on a law project
  @PostMapping("/lawprojectsvotations/{lawProject_id}/vote")
  public ResponseEntity<?> updateCitizenVote(
      @PathVariable("lawProject_id") String lawProjectId,
      @RequestParam("citizen_id") String citizenId,
      @RequestParam("vote") String wantToChange) {
        Long citizenIdLong = Long.parseLong(citizenId);
        boolean wantToChangeBool = Boolean.parseBoolean(wantToChange);
        Long lawProject_id_long = Long.parseLong(lawProjectId);
    try {
      if(managementService.vote_on_a_law_project(citizenIdLong, lawProject_id_long, wantToChangeBool)){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You already voted on this project");
      }
      return ResponseEntity.ok().body("Voted successfully");
    } catch (CitizenNotFoundException e) {
      return ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST);
    } catch (LawProjectNotFoundException e) {
      return ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST);
    } catch (LawProjectClosedException e) {
      return ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST);
    } catch (DelegateNotFoundException e) {
      return ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST);
    }
  }



  @GetMapping("/check_delegate_status")
  public ResponseEntity<Map<String, Boolean>> checkDelegateStatus() {
    Map<String, Boolean> response = new HashMap<>();
    response.put("isDelegate", citizenService.isDelegate());
    return ResponseEntity.ok(response);
  }

   /**
   * Endpoint to vote on a project in javafx (because the current citizen is not kept on service)
   */
  @PostMapping("/{citizen_id}/vote/{id}")
  public ResponseEntity<?> vote(@PathVariable Long id, @PathVariable Long citizen_id, @RequestBody String vote) {

    boolean wantToChange = vote.equals("AGAINST") ? true : false;

    try {
      boolean voted = managementService.vote_on_a_law_project(citizen_id, id, wantToChange);
      if(voted)
       return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You already voted on this project");
      return ResponseEntity.ok().body("You have successfully voted on law project with id " + id);
    } catch (LawProjectNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping("/vote/{id}")
  public ResponseEntity<?> vote(@PathVariable Long id, @RequestBody String vote) {

    boolean wantToChange = vote.equals("AGAINST") ? true : false;

    try {
      boolean voted = managementService.vote_on_a_law_project(citizenService.get_current_citizen_id(), id, wantToChange);
      if(voted)
       return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You already voted on this project");
      return ResponseEntity.ok().build();
    } catch (LawProjectNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping("/citizens")
  public ResponseEntity<?> addCitizen(@RequestBody CitizenDTO citizenDTO) {
    boolean citizenExists = citizenService.checkCitizenExistsById(citizenDTO.getId());

    try {
      if (!citizenExists)
        this.citizenService.add_citizen(citizenDTO.getName(), citizenDTO.getId(), citizenDTO.getIsDelegate());
      else
        this.citizenService.set_current_citizen_id(citizenDTO.getId());
      return ResponseEntity.ok().body("Citizen with id = " + citizenDTO.getId() + " logged in successfully!");

    } catch (Exception e) {
      return ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/citizens/{id}")
  public ResponseEntity<?> getCitizen(@PathVariable Long id) {
    try {
      CitizenDTO c = citizenService.getCitizenById(id);
      return ResponseEntity.ok().body(c);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping("/lawproject/support/{id}")
  public ResponseEntity<String> supportLawProject(@PathVariable("id") Long id) {
      Long citizenId = citizenService.get_current_citizen_id();

      if (citizenId == null) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
      }

      try {
          managementService.sign_lawProject(citizenId, id);
          return ResponseEntity.ok("You have successfully supported this project");
      } catch (AlreadSignedException e) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have already signed this project");
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
      }
  }

  /**
   * Endpoint to support a project in javafx (beacuse the current citizen is not kept on service)
   */
  @PostMapping("/{citizenId}/lawproject/sign/{id}")
  public ResponseEntity<String> signLawProject(@PathVariable("id") Long id, @PathVariable("citizenId") Long citizenId) {

      if (citizenId == null) {
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
      }

      try {
          managementService.sign_lawProject(citizenId, id);
          return ResponseEntity.ok("You have successfully supported this project");
      } catch (AlreadSignedException e) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have already signed this project");
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
      }
  }


  @PostMapping("/assign_delegate")
  public ResponseEntity<?> assignDelegate(@RequestParam("delegateIds") List<Long> delegateIds, @RequestParam("themeIds") List<Long> themeIds) {
    Long citizenId = citizenService.get_current_citizen_id();

    try {
      for (int i = 0; i < delegateIds.size(); i++) {
        Long delegateId = delegateIds.get(i);
        Long themeId = themeIds.get(i);
        managementService.chooseDelegate(delegateId, themeId, citizenId);
      }
      return ResponseEntity.ok().body("Delegates assigned successfully");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST);
    }
  }
}
