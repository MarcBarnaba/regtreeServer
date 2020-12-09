package it.map1920.regtreeServer;

import java.io.IOException;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	private Stage primaryStage;
	private AnchorPane root;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("regtreeServer");
		this.primaryStage.setResizable(false);
		loadMainView();
		setCloseAlert();

	}
	
    private void loadMainView() {
    	
    	root = new AnchorPane();
        FXMLLoader loader = null;
		try {
			loader = new FXMLLoader(Main.class.getResource("/it/map1920/regtreeServer/view/MainView.fxml"));
			root = (AnchorPane) loader.load();
    		primaryStage.setScene(new Scene(root));
    		primaryStage.show();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    }

	private void setCloseAlert() {
		Alert a = new Alert(Alert.AlertType.NONE);

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				t.consume();

				a.setAlertType(Alert.AlertType.CONFIRMATION);
				a.setTitle("Chiusura server");
				a.setContentText("Chiudere il server?");

				Optional<ButtonType> result = a.showAndWait();
				if (result.get() == ButtonType.OK) {
					Platform.exit();
					System.exit(0);
				}

			}
		});
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

}