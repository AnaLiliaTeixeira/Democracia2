package pt.ul.fc.css.project.client.presentation.control;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import pt.ul.fc.css.project.client.presentation.utils.SceneController;

public class MainPageController implements Initializable {

    SceneController sceneController = SceneController.getInstance();

    @FXML
    private Label txtCurrentUser;

    @FXML
    private Button btCheckVotations;

    @FXML
    private Button btCheckLawProjects;

    @FXML
    private Button btChooseDelegate;

    @FXML
    public void onbtCheckVotationsAction(ActionEvent event) {
        try {
            sceneController.switchScene(event, "Votations.fxml");
        }
        catch(Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText(e.getMessage());
            alert.show();
            e.printStackTrace();
        }
    }

    @FXML
    public void onbtCheckLawProjectsAction(ActionEvent event) {
        try {
            sceneController.switchScene(event, "LawProjects.fxml");
        }
        catch(Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText(e.getMessage());
            alert.show();
            e.printStackTrace();
        }
    }

    @FXML
    public void onbtChooseDelegate(ActionEvent event) {
        try {
            sceneController.switchScene(event, "ChooseDelegate.fxml");
        }
        catch(Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText(e.getMessage());
            alert.show();
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtCurrentUser.setText(sceneController.getCurrent_citizen());
    }

}
