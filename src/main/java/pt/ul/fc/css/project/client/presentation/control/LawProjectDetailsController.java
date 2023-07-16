package pt.ul.fc.css.project.client.presentation.control;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import pt.ul.fc.css.project.client.presentation.model.RestAPIClientService;
import pt.ul.fc.css.project.client.presentation.utils.SceneController;
import pt.ul.fc.css.project.server.business.services.dtos.LawProjectDTO;

public class LawProjectDetailsController implements Initializable {

    SceneController sceneController = SceneController.getInstance();

    private RestAPIClientService restService = RestAPIClientService.getInstance();

    @FXML
    private Label txtId;

    @FXML
    private Label txtTitle;

    @FXML
    private Label txtDescription;

    @FXML
    private Label txtPdf_Attachment;

    @FXML
    private Label txtClose_Date;

    @FXML
    private Label txtDelegate;

    @FXML
    private Label txtTheme;

    @FXML
    private Label txtVotation;

    @FXML
    private Button btBack;

    @FXML
    private Button btSign;

    @FXML
    private ComboBox<String> comboBoxVote;

    private ObservableList<String> obsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            Long current_id = sceneController.getCurrent_lawproject_id();
            LawProjectDTO lawProject = restService.getLawProject(current_id);

            txtId.setText(String.valueOf(current_id));
            txtTitle.setText(lawProject.getTitle());
            txtDescription.setText(lawProject.getDescription());
            txtPdf_Attachment.setText(lawProject.getPdf_attachment());

            LocalDateTime closeDate = lawProject.getClose_date();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            txtClose_Date.setText(closeDate.format(formatter));
            txtDelegate.setText(lawProject.getDelegate_name());
            txtTheme.setText(lawProject.getTheme_name());
            txtVotation.setText(lawProject.getVotation() ? "In votation" : "Not in votation");

            List<String> list = new ArrayList<>();
            if (isOnVotation(lawProject)) {
                list.add("FAVOR");
                list.add("AGAINST");

                obsList = FXCollections.observableArrayList(list);
                comboBoxVote.setItems(obsList);
                comboBoxVote.setVisible(true);
            } else {
                comboBoxVote.setVisible(false);
            }

            obsList = FXCollections.observableArrayList(list);
            comboBoxVote.setItems(obsList);


        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            sceneController.switchScene(event, "MainPage.fxml");
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    public void onBtSignAction() {
        try {
            String response = restService.signLawProject(sceneController.getCurrent_lawproject_id(), sceneController.getCurrent_citizen_id());

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("SUCCESS");
            alert.setContentText(response);
            alert.show();

        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    public void onComboVoteAction() {

        String vote = comboBoxVote.getSelectionModel().getSelectedItem();
        try {
            String response = restService.voteLawProject(sceneController.getCurrent_lawproject_id(), sceneController.getCurrent_citizen_id(), vote);

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("SUCCESS");
            alert.setContentText(response);
            alert.show();

        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    public boolean isOnVotation(LawProjectDTO lp) {
        return lp.getVotation();
    }
}
