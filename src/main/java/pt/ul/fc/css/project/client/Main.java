package pt.ul.fc.css.project.client;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("Democracia 2.0");
			String prefix = "/pt/ul/fc/css/project/client/presentation/view/";

			URL url = getClass().getResource(prefix + "Login.fxml");
			Parent loginFXML = FXMLLoader.load(url);
			Scene loginScene = new Scene(loginFXML, 700, 700);

			primaryStage.setMinWidth(700);
			primaryStage.setMinHeight(700);
			primaryStage.setScene(loginScene);
			primaryStage.show();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
