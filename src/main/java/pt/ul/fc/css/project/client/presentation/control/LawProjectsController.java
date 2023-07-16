package pt.ul.fc.css.project.client.presentation.control;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pt.ul.fc.css.project.client.presentation.model.RestAPIClientService;
import pt.ul.fc.css.project.client.presentation.utils.SceneController;
import pt.ul.fc.css.project.server.business.services.dtos.LawProjectDTO;

public class LawProjectsController implements Initializable {

    SceneController sceneController = SceneController.getInstance();

    private RestAPIClientService restService;

    @FXML
    private TableView<LawProjectDTO> tableView;

    @FXML
    private TableColumn<LawProjectDTO, String> colTitle;

    @FXML
    private TableColumn<LawProjectDTO, String> colDescription;

    @FXML
    private TableColumn<LawProjectDTO, String> colPdf_attachment;

    @FXML
    private TableColumn<LawProjectDTO, LocalDateTime> colCloseDate;

    @FXML
    private TableColumn<LawProjectDTO, String> colDelegateName;

    @FXML
    private TableColumn<LawProjectDTO, String> colThemeName;

    @FXML
    private Button btBack;

    @FXML
    private Button btAdd;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            restService = RestAPIClientService.getInstance();
            List<LawProjectDTO> listLP = restService.getAllNonExpiredLawProjects();

            ObservableList<LawProjectDTO> observableList = FXCollections.observableArrayList(listLP);

            colTitle.setCellValueFactory(new PropertyValueFactory<LawProjectDTO, String>("title"));
            colDescription.setCellValueFactory(new PropertyValueFactory<LawProjectDTO, String>("description"));
            colPdf_attachment.setCellValueFactory(new PropertyValueFactory<LawProjectDTO, String>("pdf_attachment"));
            colCloseDate.setCellValueFactory(new PropertyValueFactory<LawProjectDTO, LocalDateTime>("close_date"));
            colDelegateName.setCellValueFactory(new PropertyValueFactory<LawProjectDTO, String>("delegate_name"));
            colThemeName.setCellValueFactory(new PropertyValueFactory<LawProjectDTO, String>("theme_name"));
            tableView.setItems(observableList);

            tableView.setOnMousePressed(event -> {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 1) {
                    LawProjectDTO selectedLawProject = tableView.getSelectionModel().getSelectedItem();
                    if (selectedLawProject != null) {
                        try {
                            sceneController.setCurrent_lawproject_id(selectedLawProject.getId());
                            sceneController.switchScene(event, "LawProjectDetails.fxml");
                        } catch (IOException e) {
                            Alert alert = new Alert(AlertType.ERROR);
                            alert.setTitle("Erro");
                            alert.setContentText(e.getMessage());
                            alert.show();
                            e.printStackTrace();
                        }
                    }
                }
            });

        } catch (Exception e) {
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
    public void onBtAddAction(ActionEvent event) {
        try {
            sceneController.switchScene(event, "AddLawProject.fxml");
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

}
