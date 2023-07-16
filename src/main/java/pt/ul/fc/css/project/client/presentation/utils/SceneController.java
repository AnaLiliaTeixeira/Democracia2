package pt.ul.fc.css.project.client.presentation.utils;

import java.io.IOException;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneController {

    private Stage stage;
    private Scene scene;
    String prefix = "/pt/ul/fc/css/project/client/presentation/view/";

    private static SceneController instance;

    private String current_citizen;

    private Long current_citizen_id;

    private Long current_lawproject_id;

    private SceneController() {}

    public static SceneController getInstance() {
        if (instance == null)
            instance = new SceneController();
        return instance;
    }

    public void switchScene(Event event, String sceneName) throws IOException {
        Object source = event.getSource();
        if (source instanceof Node) {
            Parent root = FXMLLoader.load(getClass().getResource(prefix + sceneName));
            stage = (Stage) ((Node) source).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    public String getCurrent_citizen() {
        return current_citizen;
    }
    public void setCurrent_citizen(String name) {
        current_citizen = name;
    }

    public Long getCurrent_citizen_id() {
        return current_citizen_id;
    }
    public void setCurrent_citizen_id(Long id) {
        current_citizen_id = id;
    }

    public Long getCurrent_lawproject_id() {
        return current_lawproject_id;
    }
    public void setCurrent_lawproject_id(Long id) {
        current_lawproject_id = id;
    }

}
