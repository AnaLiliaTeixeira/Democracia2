package pt.ul.fc.css.project.client.presentation.control;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import pt.ul.fc.css.project.client.presentation.utils.SceneController;

public class AddLawProjectController implements Initializable {
    
    SceneController sceneController = SceneController.getInstance();

    @FXML
    private Button btBack;

    @FXML
    private Button btAdd;



    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @FXML
    public void goBack(ActionEvent event) {
        try {
            sceneController.switchScene(event, "LawProjects.fxml");
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    public void onBtAddAction(ActionEvent event) {
        try {
            sceneController.switchScene(event, "LawProjects.fxml");
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("SUCCESS");
            alert.setContentText("Law Project added successfully!");
            alert.show();

        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }
}
