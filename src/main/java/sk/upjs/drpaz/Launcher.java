package sk.upjs.drpaz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
		LoginController loginController = new LoginController();
		fxmlLoader.setController(loginController);
		Parent parent = fxmlLoader.load();
		Scene scene = new Scene(parent);
		stage.setScene(scene);
		stage.setTitle("Login");
		stage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}
