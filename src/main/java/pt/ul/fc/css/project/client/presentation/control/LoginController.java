package pt.ul.fc.css.project.client.presentation.control;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import pt.ul.fc.css.project.client.presentation.model.RestAPIClientService;
import pt.ul.fc.css.project.client.presentation.utils.SceneController;
import pt.ul.fc.css.project.server.business.services.dtos.CitizenDTO;

public class LoginController implements Initializable {

    private RestAPIClientService restService = RestAPIClientService.getInstance();
    SceneController sceneController = SceneController.getInstance();

    @FXML
    private TextField txtId;

    @FXML
    private Button btCreateCitizen;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @FXML
    public void onBtCreateCitizenAction(ActionEvent event) {

        try {

            CitizenDTO citizen = restService.getCitizen(Long.parseLong(txtId.getText()));
            citizen.setId(Long.parseLong(txtId.getText()));
            sceneController.setCurrent_citizen_id(citizen.getId());
            sceneController.setCurrent_citizen(citizen.getName());
            sceneController.switchScene(event, "MainPage.fxml");

        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("Citizen with id " + txtId.getText() + " doesn't exist.");
            alert.show();
        }
    }
}
