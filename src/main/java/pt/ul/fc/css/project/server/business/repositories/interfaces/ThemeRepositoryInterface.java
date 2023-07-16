package pt.ul.fc.css.project.server.business.repositories.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.ul.fc.css.project.server.business.entities.Theme;

/** The repository that deals of themes objects */
@Repository
public interface ThemeRepositoryInterface extends JpaRepository<Theme, Long> {}
